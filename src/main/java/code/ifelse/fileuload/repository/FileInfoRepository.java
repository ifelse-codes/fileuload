package code.ifelse.fileuload.repository;

import code.ifelse.fileuload.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Boolean existsByUserAndFile(@Param("user") String user,@Param("file") String file);
    List<FileInfo> findAllByUser(@Param("user") String user);
}