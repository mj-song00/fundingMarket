package market.fundingmarket.domain.creator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.config.PasswordEncoder;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.user.dto.request.SignupRequest;
import market.fundingmarket.domain.user.enums.UserRole;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatorServiceImpl implements CreatorService {
    private final CreatorRepository creatorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createCreator(SignupRequest signupRequest) {
        Optional<Creator> userByEmail = creatorRepository.findByEmail(signupRequest.getEmail());
        Optional<Creator> userByNickname = creatorRepository. findByNickName(signupRequest.getNickName());

        if (userByEmail.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        if (userByNickname.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }


        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.CREATOR;

        Creator creator = new Creator(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                encodedPassword,
                userRole
        );

        creatorRepository.save(creator);
    }
}
