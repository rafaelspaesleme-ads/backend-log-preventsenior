package br.com.preventsr.logs.resources.v1.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
@ApiModel
public class FileLogDTO {
    private MultipartFile file;
    private Long sizeFile;
}
