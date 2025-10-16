package market.fundingmarket.common.authenticable;

import market.fundingmarket.domain.user.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Authenticatable {
    UUID getId();
    String getEmail();
    String getPassword();
    UserRole getRole();
    LocalDateTime isDeleted();
}
