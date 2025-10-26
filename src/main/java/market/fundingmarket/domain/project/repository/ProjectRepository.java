package market.fundingmarket.domain.project.repository;

import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findById(Long projectId);

    List<Project> findByCategory(Category categoryKey);
}
