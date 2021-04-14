package br.com.preventsr.logs.utils.functions;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ChecksFunctions {
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
}
