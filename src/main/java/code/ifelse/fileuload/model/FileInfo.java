package code.ifelse.fileuload.model;

import com.google.cloud.storage.BlobId;

import java.util.Map;

public class FileInfo {
    private BlobId blobId;
    private Map<String,String> metadata;

    public FileInfo(BlobId blobId, Map<String, String> metadata) {
        this.blobId = blobId;
        this.metadata = metadata;
    }

    public BlobId getBlobId() {
        return blobId;
    }

    public void setBlobId(BlobId blobId) {
        this.blobId = blobId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
