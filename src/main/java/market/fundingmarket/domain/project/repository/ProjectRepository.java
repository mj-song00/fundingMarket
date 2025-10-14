package market.fundingmarket.domain.project.repository;

import market.fundingmarket.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
