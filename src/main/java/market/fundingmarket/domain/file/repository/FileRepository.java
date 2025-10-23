package market.fundingmarket.domain.file.repository;

import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository< File, Long> {
    List<File> findByProjectId(Long projectId);

    Optional<File> findByProjectAndIsThumbnailTrue(Project project);

    List<File> findByProjectIdAndIsThumbnailTrue(long id);
}
