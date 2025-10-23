package market.fundingmarket.domain.file.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateFileRequest {
    private Long fileId;
    private MultipartFile file;
}
