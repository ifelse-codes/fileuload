package code.ifelse.fileuload;

import code.ifelse.fileuload.model.FileInfo;
import code.ifelse.fileuload.repository.FileInfoRepository;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@EnableJpaRepositories
public class FileuloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileuloadApplication.class, args);
    }
    private static final String GOOGLE_CLOUD_CRED = "C:\\fiery-strategy-277408-7b5884632a24.json";
    private static final String BUCKET_NAME = "suman-bucket";
    private static final Integer URL_TTL = 1;
    private Storage storage;
    private Bucket bucket;

    @Autowired
    FileInfoRepository repository;

    @PostConstruct
    public void initIt() throws Exception {
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(GOOGLE_CLOUD_CRED));
        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("fiery-strategy-277408").build().getService();
        bucket = storage.get(BUCKET_NAME);
        if (bucket == null) {
            bucket = storage.create(BucketInfo.of(BUCKET_NAME));
        }
    }

    @PostMapping("/file/{user}")
    FileInfo uploadFile(@RequestParam MultipartFile file, @PathVariable final String user) throws IOException {
        String fileName = file.getOriginalFilename();

        Boolean isExists = repository.existsByUserAndFile(user,fileName);
        if(isExists) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file name already exists");

        byte[] bytes = file.getBytes();
        Blob blob = bucket.create(fileName, bytes);

        FileInfo fileInfo = new FileInfo(user, blob.getBlobId().getName());
        return repository.save(fileInfo);
    }

    @GetMapping("/file/{user}/{file}")
    String getFileURL(@PathVariable final String file, @PathVariable final String user) {

        Boolean isExists = repository.existsByUserAndFile(user,file);
        if(!isExists) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "requested file does not exists");

        URL signedUrl = storage.signUrl(BlobInfo.newBuilder(BUCKET_NAME, file).build(), URL_TTL, TimeUnit.MINUTES);
        return signedUrl.toString();
    }

    @GetMapping("/file/{user}")
    List<FileInfo> getFiles(@PathVariable final String user) {
        List<FileInfo> fileInfos = repository.findAllByUser(user);
        return fileInfos;
    }
}
