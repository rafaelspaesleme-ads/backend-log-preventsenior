package br.com.preventsr.logs.resources.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
public class ResponseDTO {
    private Object data;
    private Object error;
    private Integer statusHttp;
    private String message;
    private LocalDateTime dateResponse;
}
