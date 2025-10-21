package market.fundingmarket.domain.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateFundingRequest {
    private String title; // 제목

    private String content; // 본문 글

    private List<Long> deleteImageIds; // 삭제할 기존 이미지 ID
}
