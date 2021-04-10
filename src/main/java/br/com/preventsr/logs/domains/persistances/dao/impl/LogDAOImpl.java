package br.com.preventsr.logs.domains.persistances.dao.impl;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.domains.persistances.repository.LogJDBCRepository;
import br.com.preventsr.logs.domains.persistances.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LogDAOImpl implements LogDAO {

    private static final Logger log = LoggerFactory.getLogger(LogDAOImpl.class);

    private final LogRepository logRepository;
    private final LogJDBCRepository logJDBCRepository;

    public LogDAOImpl(LogRepository logRepository, LogJDBCRepository logJDBCRepository) {
        this.logRepository = logRepository;
        this.logJDBCRepository = logJDBCRepository;
    }

    @Override
    public List<LogEntity> bulkInsert(List<LogEntity> logEntityList) {
        return logRepository.saveAll(logEntityList);
    }

    @Override
    public Boolean insertOrEditLogDefault(LogEntity logEntity) {
        try {
            logRepository.save(logEntity);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean insertOrEditLogCustom(LogEntity logEntity) {
        try {
            logJDBCRepository.save(logEntity);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<LogEntity> listAllLog() {
        List<LogEntity> logEntities = new ArrayList<>();
        logJDBCRepository.findAll().forEach(logEntities::add);
        return logEntities;
    }

    @Override
    public List<LogEntity> listAllByActiveLog() {
        List<LogEntity> logEntities = new ArrayList<>();
        logJDBCRepository.findAllByActive(true).forEach(logEntities::add);
        return logEntities;
    }

    @Override
    public List<LogEntity> listAllByNameLog(String nameLog) {
        List<LogEntity> logEntities = new ArrayList<>();
        logJDBCRepository.findAllByFileNameContains(nameLog).forEach(logEntities::add);
        return logEntities;
    }

    @Override
    public List<LogEntity> listAllByIpLog(String ipLog) {
        List<LogEntity> logEntities = new ArrayList<>();
        logJDBCRepository.findAllByIpContains(ipLog).forEach(logEntities::add);
        return logEntities;
    }

    @Override
    public Optional<LogEntity> findByIdLog(String idLog) {
        return logJDBCRepository.findById(idLog);
    }

    @Override
    public Boolean deleteLog(String idLog) {
        logJDBCRepository.deleteById(idLog);
        return logJDBCRepository.findById(idLog).isPresent();
    }
}
