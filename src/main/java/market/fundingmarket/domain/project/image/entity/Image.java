package market.fundingmarket.domain.project.image.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;

@Getter
@Entity
@Table(name = "image")
@NoArgsConstructor
public class Image extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String imageUrl;

    public Image(long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
