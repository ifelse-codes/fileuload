package code.ifelse.fileuload.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FileInfo {
    private @Id
    @GeneratedValue
    Long id;
    private String user;
    private String file;

    public FileInfo() {
    }

    public FileInfo(String user, String file) {
        this.file = file;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}