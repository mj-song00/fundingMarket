package market.fundingmarket.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "제목을 입력해 주세요")
    private String title;

    @NotBlank(message = "카테고리를 입력해주세요.")
    private String category;

    private String contents;

    private String image;

    private Long fundingAmount;

    private String fundingSchedule;

}
