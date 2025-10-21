package market.fundingmarket.domain.file.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.entity.Project;

@Getter
@Entity
@Table(name = "image")
@NoArgsConstructor
public class File extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String originalFileName; // 클라이언트가 업로드한 원본 파일명

    @Column(nullable = false)
    private String imageUrl;         // 로컬 경로 또는 S3 URL

    @Column(nullable = false)
    private boolean isThumbnail = false; // 썸네일 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public File(String imageUrl, String originalFileName, Project project, boolean isThumbnail) {
        this.imageUrl = imageUrl;
        this.originalFileName = originalFileName;
        this.project = project;
        this.isThumbnail = isThumbnail;
    }
}
