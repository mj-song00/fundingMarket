package market.fundingmarket.domain.project.repository;

import market.fundingmarket.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectId(Long portfolioId);
}
