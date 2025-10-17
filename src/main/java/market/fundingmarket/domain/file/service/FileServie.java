package market.fundingmarket.domain.file.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.file.repository.FileRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.repository.ProjectRepository;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServie {
    private final CreatorRepository creatorRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;

    @Transactional
    public List<String> saveFile(List<MultipartFile> file, AuthUser authUser, Long projectId){
        getUser(authUser.getId());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        Path uploadDir = Paths.get("upload");
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
            }
        }

        List<File> imageEntities = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile multipartFile : file) {
            try {
                String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
                Path path = uploadDir.resolve(fileName);
                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                File image = new File(fileName, multipartFile.getOriginalFilename(), project);
                imageEntities.add(image);

                filePaths.add(path.toAbsolutePath().toString());
            } catch (IOException e) {
                throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
            }
        }

        fileRepository.saveAll(imageEntities);
        return filePaths;
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
