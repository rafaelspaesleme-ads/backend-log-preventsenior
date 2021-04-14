package br.com.preventsr.logs.utils.functions;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ValidatorsFunctions {
    public static Boolean chkUpdate(LogDTO logDTO, LogEntity logEntity) {
        if (logDTO.getActive().equals(logEntity.getActive())
                && logDTO.getDateTime().equals(logEntity.getDateTime())
                && logDTO.getFileName().equals(logEntity.getFileName())
                && logDTO.getIp().equals(logEntity.getIp())
                && logDTO.getRequest().equals(logEntity.getRequest())
                && logDTO.getStatusHttp().equals(logEntity.getStatusHttp())
                && logDTO.getUserAgent().equals(logEntity.getUserAgent())) {
            return false;
        } else if (logDTO.getActive().equals(logEntity.getActive())
                || logDTO.getDateTime().equals(logEntity.getDateTime())
                || logDTO.getFileName().equals(logEntity.getFileName())
                || logDTO.getIp().equals(logEntity.getIp())
                || logDTO.getRequest().equals(logEntity.getRequest())
                || logDTO.getStatusHttp().equals(logEntity.getStatusHttp())
                || logDTO.getUserAgent().equals(logEntity.getUserAgent())) {
            return true;
        } else {
            return true;
        }
    }

    public static PageRequest chkPageable(Integer page, Integer linesPerPage, String orderBy, String direction) {
        return PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
    }

    public static long chkLimited(Long limited) {
        return limited == null ? 1000L : limited;
    }

    public static boolean chkRequest(String request) {

        if (request.toUpperCase().contains("GET")) {
            return true;
        } else if (request.toUpperCase().contains("POST")) {
            return true;
        } else if (request.toUpperCase().contains("PATCH")) {
            return true;
        } else if (request.toUpperCase().contains("PUT")) {
            return true;
        } else if (request.toUpperCase().contains("DELETE")) {
            return true;
        } else if (request.toUpperCase().contains("OPTIONS")) {
            return true;
        } else if (request.toUpperCase().contains("TRACE")) {
            return true;
        } else if (request.toUpperCase().contains("CONNECT")) {
            return true;
        } else if (request.toUpperCase().contains("HEAD")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean chkStatusHttp(Integer statusHttp) {
        return HttpStatus.resolve(statusHttp) != null;
    }
}
