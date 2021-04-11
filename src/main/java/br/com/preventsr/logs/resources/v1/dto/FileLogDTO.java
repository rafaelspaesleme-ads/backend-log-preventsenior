package br.com.preventsr.logs.resources.v1.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
public class FileLogDTO {
    private MultipartFile file;
    private Long sizeFile;
}
