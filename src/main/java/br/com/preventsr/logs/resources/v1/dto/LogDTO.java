package br.com.preventsr.logs.resources.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
public class LogDTO {
    private String id;
    private String fileName;
    private LocalDateTime dateTime;
    private String ip;
    private String request;
    private String statusHttp;
    private String userAgent;
    private Boolean active;
}
