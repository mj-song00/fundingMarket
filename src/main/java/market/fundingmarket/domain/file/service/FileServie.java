package market.fundingmarket.domain.file.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.file.repository.FileRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.enums.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServie {
    private final CreatorRepository creatorRepository;
    private final FileRepository fileRepository;


    private final Path uploadDir = Paths.get("upload");

    @PostConstruct
    private void init() {
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new RuntimeException("업로드 폴더 생성 실패", e);
            }
        }
    }

    // 이미지 추가
    @Transactional
    public List<String> saveFile(List<MultipartFile> addImages, AuthUser authUser, Project project) {
        getUser(authUser.getId());

        if (addImages == null || addImages.isEmpty()) return Collections.emptyList();

        List<File> newImages = new ArrayList<>();
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : addImages) {
            try {
                String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
                Path path = uploadDir.resolve(fileName);
                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                File image = new File(path.toAbsolutePath().toString(), multipartFile.getOriginalFilename(), project, false);
                newImages.add(image);
                fileUrls.add(path.toAbsolutePath().toString());
            } catch (IOException e) {
                throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
            }
        }

        fileRepository.saveAll(newImages);
        return fileUrls;
    }


    // 썸네일 등록 , 교체
    @Transactional
    public String updateThumbnail(MultipartFile thumbnail, Project project) {
        if (thumbnail == null) return null;

        try {
            String fileName = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename();
            Path path = uploadDir.resolve(fileName);
            Files.copy(thumbnail.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 기존 썸네일이 있으면 삭제
            fileRepository.findByProjectAndIsThumbnailTrue(project).ifPresent(existing -> {
                try {
                    Files.deleteIfExists(Paths.get(existing.getImageUrl()));
                } catch (IOException ignored) {}
                fileRepository.delete(existing);
            });

            // 새 썸네일 저장
            File newThumbnail = new File(path.toAbsolutePath().toString(), thumbnail.getOriginalFilename(), project, true);
            fileRepository.save(newThumbnail);
            return path.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
        }
    }

    private  Creator getUser(UUID id) {
        Creator user = creatorRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.CREATOR_NOT_FOUND));

        if (user.getUserRole() != UserRole.CREATOR){
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }
}
