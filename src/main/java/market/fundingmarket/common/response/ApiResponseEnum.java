package market.fundingmarket.common.response;

import lombok.Getter;

@Getter
public enum ApiResponseEnum {
    SIGNUP_SUCCESS("회원가입 완료"),
    PASSWORD_CHANGED_SUCCESS("비밀번호 변경이 완료되었습니다."),
    PROFILE_RETRIEVED_SUCCESS("프로필 조회가 완료되었습니다."),
    NICKNAME_CHANGED_SUCCESS("닉네임 변경이 완료되었습니다."),
    USER_DELETED_SUCCESS("회원 탈퇴가 완료되었습니다."),
    REGISTRATION_SUCCESS("펀딩 프로젝트가 등록되었습니다."),
    UPDATE_SUCCESS("수정이 완료되었습니다."),
    GET_SUCCESS("조회 성공"),
    SELECT_REWARD_SUCCESS("리워드 선택이 완료되었습니다."),
    FUNDING_CANCEL_SUCCESS("후원이 취소되었습니다.");

    private final String message;

    ApiResponseEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
