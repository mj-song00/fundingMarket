package market.fundingmarket.domain.file.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.fundingmarket.common.config.S3Config;
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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServie {
    private final CreatorRepository creatorRepository;
    private final FileRepository fileRepository;
    private final S3Client s3Client;
    private final S3Config s3Config;



    @Transactional
    public List<String> saveFile(List<MultipartFile> addImages, AuthUser authUser, Project project) {
        getUser(authUser.getId());

        if (addImages == null || addImages.isEmpty()) return Collections.emptyList();

        List<File> newImages = new ArrayList<>();
        List<String> fileUrls = new ArrayList<>();

        String bucket =  s3Config.getBucket();
        String region = "ap-northeast-2";

        for (MultipartFile multipartFile : addImages) {
            try {
                // S3에 저장될 파일 이름
                String imageName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

                // PutObjectRequest 생성
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(imageName)
                        .contentType(multipartFile.getContentType())
                        .contentLength(multipartFile.getSize())
                        .build();

                PutObjectResponse response = s3Client.putObject(
                        putObjectRequest,
                        RequestBody.fromBytes(multipartFile.getBytes())
                );

                if (!response.sdkHttpResponse().isSuccessful()) {
                    throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
                }

                // S3 URL 생성
                String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, imageName);

                // 파일명 추출
                String originalFileName = multipartFile.getOriginalFilename();
//                String extension = originalFileName != null && originalFileName.contains(".")
//                        ? originalFileName.substring(originalFileName.lastIndexOf(".") + 1)
//                        : "";

                // DB 저장용 엔티티 생성
                File file = new File(
                        imageUrl,   // S3 URL
                        originalFileName,   // 실제 파일명
                        project,
                        false
                );
                newImages.add(file);

            } catch (IOException e) {
                throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
            }
        }


        // DB에 일괄 저장
        fileRepository.saveAll(newImages);
        return fileUrls;
    }


    // 썸네일 등록 , 교체
    @Transactional
    public String updateThumbnail(MultipartFile thumbnail, Project project) {
        if (thumbnail == null) return null;

        String bucket = s3Config.getBucket();   // S3 버킷
        String region = s3Config.getRegion();    // URL 생성용
        try {
            // S3에 저장될 파일 이름
            String imageName = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename();

            // S3 업로드 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageName)
                    .contentType(thumbnail.getContentType())
                    .contentLength(thumbnail.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(thumbnail.getBytes())
            );

            if (!response.sdkHttpResponse().isSuccessful()) {
                throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
            }

            // 새 썸네일 URL
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, imageName);

            // 기존 썸네일 삭제 (DB에서만 삭제, S3 파일 삭제는 선택 사항)
            fileRepository.findByProjectAndIsThumbnailTrue(project).ifPresent(existing -> {
                fileRepository.delete(existing);
            });

            // 새 썸네일 DB 저장
            File newThumbnail = new File(
                    imageUrl,                      // S3 URL
                    thumbnail.getOriginalFilename(),
                    project,
                    true                            // isThumbnail
            );
            fileRepository.save(newThumbnail);

            return imageUrl;

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
