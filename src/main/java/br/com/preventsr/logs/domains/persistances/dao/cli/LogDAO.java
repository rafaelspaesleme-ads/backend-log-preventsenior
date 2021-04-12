package br.com.preventsr.logs.domains.persistances.dao.cli;

import br.com.preventsr.logs.domains.entities.LogEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface LogDAO {
    Boolean bulkInsert(List<LogEntity> logEntityList);
    Boolean bulkInsert(LogEntity logEntity);
    Boolean insertOrEditLogDefault(LogEntity logEntity);
    Boolean insertOrEditLogCustom(LogEntity logEntity);
    List<LogEntity> listAllLog();
    Page<LogEntity> listAllByActiveLog(Integer page, Integer linesPerPage, String orderBy, String direction, Long limited);
    Page<LogEntity> listAllByUserAgentLog(String userAgent, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited);
    Page<LogEntity> listAllByIpLog(String ipLog, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited);
    Optional<LogEntity> findByIdLog(String idLog);
    Boolean deleteLog(String idLog);
}
