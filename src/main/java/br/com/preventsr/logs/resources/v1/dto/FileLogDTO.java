package br.com.preventsr.logs.resources.v1.dto;

import br.com.preventsr.logs.utils.enums.TypeUploadEnum;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
public class FileLogDTO {
    private String nameFile;
    private String modifiedDate;
    @Enumerated(EnumType.STRING)
    private TypeUploadEnum typeUpload;
    private MultipartFile multipartFile;
    private Long sizeFile;
}
