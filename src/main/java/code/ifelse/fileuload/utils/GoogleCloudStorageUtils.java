package code.ifelse.fileuload.utils;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

public class GoogleCloudStorageUtils {

    private static final String SLASH = "/";

    public static Boolean doesFolderExist(Storage storage, String bucketName, String folderName) {
        Page<Blob> blobs = storage.list(
                bucketName,
                Storage.BlobListOption.prefix(formatFolderName(folderName)),
                Storage.BlobListOption.pageSize(1)
        );
        return blobs.getValues().iterator().hasNext();
    }

    public static void createFolder(Bucket bucket, String folderName) {
        bucket.create(formatFolderName(folderName), "".getBytes());
    }

    /**
     * for google cloud storage folder is abstracted as any other object but with a trailing slash.
     * https://stackoverflow.com/questions/61089238/how-do-you-check-if-a-folder-exists-in-google-cloud-storage-using-java
     *
     * @param folderName
     * @return
     */
    private static String formatFolderName(String folderName) {
        return folderName + SLASH;
    }
}
