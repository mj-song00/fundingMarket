package market.fundingmarket.domain.file.repository;

import market.fundingmarket.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository< File, Long> {
    List<File> findByProjectId(Long projectId);
}
