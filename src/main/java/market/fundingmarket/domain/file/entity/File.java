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

    @Column
    private String imageUrl;

    @Column
    private String imageName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;


    public File(String imageUrl, String imageName,  Project project) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.project = project;
    }
}
