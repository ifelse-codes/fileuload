package code.ifelse.fileuload;

import code.ifelse.fileuload.model.FileInfo;
import code.ifelse.fileuload.utils.GoogleCloudStorageUtils;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
public class FileuloadApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FileuloadApplication.class, args);
    }

    private static final String GOOGLE_CLOUD_CRED = "C:\\fiery-strategy-277408-7b5884632a24.json";
    private static final String BUCKET_NAME = "suman-bucket";
    private static final Integer URL_TTL = 1;
    private Storage storage;
    private Bucket bucket;

    @PostConstruct
    public void initIt() throws Exception {
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(GOOGLE_CLOUD_CRED));
        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("fiery-strategy-277408").build().getService();
        bucket = storage.get(BUCKET_NAME);
        if (bucket == null) {
            bucket = storage.create(BucketInfo.of(BUCKET_NAME));
        }
    }

    @PostMapping("/cloud/{folder}")
    public FileInfo uploadFile(@RequestParam MultipartFile file,
                               @RequestParam Map<String, String> metadata,
                               @PathVariable final String folder) throws IOException {

        Boolean folderExist = GoogleCloudStorageUtils.doesFolderExist(storage, BUCKET_NAME, folder);
        if (!folderExist) {
            GoogleCloudStorageUtils.createFolder(bucket, folder);
        }

        String fileName = file.getOriginalFilename();
        String fileNameWithTimeStamp = Instant.now().getEpochSecond() + "_" + fileName;
        String fileNameWithFolder = folder + "/" + fileNameWithTimeStamp;

        Blob blob = bucket.create(fileNameWithFolder, file.getBytes());
        blob.toBuilder().setMetadata(metadata).build().update();
        blob = bucket.get(fileNameWithFolder);

        return new FileInfo(blob.getBlobId(), blob.getMetadata());
    }

    @GetMapping("/cloud/{folder}/{file}")
    String getFileURL(@PathVariable final String file, @PathVariable final String folder) {
        URL signedUrl = storage.signUrl(BlobInfo.newBuilder(BUCKET_NAME, folder + "/" + file).build(), URL_TTL, TimeUnit.MINUTES);
        return signedUrl.toString();
    }
}
