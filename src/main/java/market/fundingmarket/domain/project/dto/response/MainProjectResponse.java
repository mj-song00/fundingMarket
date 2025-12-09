package market.fundingmarket.domain.project.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MainProjectResponse {
    private final long id;
    private final String title;
    private final String thumbnailUrl;

}
