package market.fundingmarket.domain.project.enums;

import lombok.Getter;

@Getter
public enum Category {
    GAME("게임"),
    HOME("홈"),
    LIVING("리빙"),
    ART("아트"),
    PET("애완동물"),
    FOOD("음식"),
    BOOK("도서"),
    FASHION("의류"),
    TECH("테크"),
    BEAUTY("화장품"),
    MUSIC("음반"),
    MOVIE("영화"),
    TRAVEL("여행"),
    CAR("차량");


    private final String message;

    Category(String message) {
        this.message = message;
    }
}
