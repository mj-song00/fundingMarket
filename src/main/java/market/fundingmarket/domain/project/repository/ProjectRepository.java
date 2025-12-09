package market.fundingmarket.domain.project.repository;

import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.enums.FundingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

//    Optional<Project> findById(Long projectId);

    List<Project> findByCategory(Category categoryKey);

    List<Project> findByStatus(FundingStatus fundingStatus);

    @Query(
            value = "SELECT p.id, p.title, p.collected_amount, p.funding_amount, p.end_date, " +
                    // 💡 imageUrl 필드에 해당하는 DB 컬럼 이름인 'image_url' 사용
                    "       f.image_url AS thumbnail_url " +
                    "FROM project p " +
                    // 💡 File 테이블 이름이 'file'이라고 가정하고, 'is_thumbnail' 컬럼 사용
                    "LEFT JOIN file f ON f.project_id = p.id AND f.is_thumbnail = TRUE " + // TRUE는 DB에 따라 1로 바뀔 수 있음
                    "ORDER BY " +
                    "    (p.collected_amount / p.funding_amount) DESC, " +
                    "    p.end_date ASC ",
                 //   "LIMIT :limit",
            nativeQuery = true
    )
    List<Object[]> findTopProjectsByCalculatedRate();
}
