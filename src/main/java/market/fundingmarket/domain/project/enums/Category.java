package market.fundingmarket.domain.project.enums;

import lombok.Getter;

@Getter
public enum Category {
   GAME("게임"),
    HOME("홈"),
    LIVING("리빙"),
    ART("아트"),
    PET("애완동물"),
    WEBTOON("웹툰"),
    GOODS("굿즈"),
    CLOTHES("의류"),
    TECH("테크"),
    PERFUME("향수"),
    MUSIC("음반"),
    BAG("가방"),
    BEAUTY("미용");


    private final String message;

    Category(String message) {
        this.message = message;
    }
}
