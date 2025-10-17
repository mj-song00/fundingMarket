package market.fundingmarket.domain.file.repository;

import market.fundingmarket.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository< File, Long> {
}
