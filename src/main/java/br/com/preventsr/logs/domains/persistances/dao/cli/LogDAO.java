package br.com.preventsr.logs.domains.persistances.dao.cli;

import br.com.preventsr.logs.domains.entities.LogEntity;

import java.util.List;
import java.util.Optional;

public interface LogDAO {
    List<LogEntity> bulkInsert(List<LogEntity> logEntityList);
    Boolean insertOrEditLogDefault(LogEntity logEntity);
    Boolean insertOrEditLogCustom(LogEntity logEntity);
    List<LogEntity> listAllLog();
    List<LogEntity> listAllByActiveLog();
    List<LogEntity> listAllByNameLog(String nameLog);
    List<LogEntity> listAllByIpLog(String ipLog);
    Optional<LogEntity> findByIdLog(String idLog);
    Boolean deleteLog(String idLog);
}
