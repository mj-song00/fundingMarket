package market.fundingmarket.domain.project.dto.response;

import lombok.Getter;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.Category;

@Getter
public class ProjectListResponse {
    private final Long id;
    private final String title;
    private final String contents;
    private final String thumbnailUrl;
    private final Category category;
    private final int collectedAmount;

    public ProjectListResponse(Project project, String thumbnailUrl) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.contents = project.getContents();
        this.thumbnailUrl = thumbnailUrl;
        this.category = project.getCategory();
        this.collectedAmount = project.getCollectedAmount();
    }
}