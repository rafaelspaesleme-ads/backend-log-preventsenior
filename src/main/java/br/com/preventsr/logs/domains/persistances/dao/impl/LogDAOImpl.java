package br.com.preventsr.logs.domains.persistances.dao.impl;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.domains.persistances.repository.LogJDBCRepository;
import br.com.preventsr.logs.domains.persistances.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.preventsr.logs.utils.functions.ValidatorsFunctions.chkLimited;
import static br.com.preventsr.logs.utils.functions.ValidatorsFunctions.chkPageable;

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
    public Boolean bulkInsert(List<LogEntity> logEntityList) {
        try {
            logRepository.saveAll(logEntityList);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean bulkInsert(LogEntity logEntity) {
        try {
            logRepository.save(logEntity);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
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
        try {
            List<LogEntity> logEntities = new ArrayList<>();
            logJDBCRepository.findAll().forEach(logEntities::add);
            return logEntities;
        } catch (Exception e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Page<LogEntity> listAllByActiveLog(Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        try {
            return new PageImpl<>(logJDBCRepository
                    .findAllByActive(true, chkPageable(page, linesPerPage, orderBy, direction))
                    .stream()
                    .limit(chkLimited(limited))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Page.empty();
        }
    }

    @Override
    public Page<LogEntity> listAllByUserAgentLog(String userAgent, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        try {
            return new PageImpl<>(logJDBCRepository
                    .findAllByUserAgentContains(userAgent, chkPageable(page, linesPerPage, orderBy, direction))
                    .stream()
                    .limit(chkLimited(limited))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Page.empty();
        }
    }

    @Override
    public Page<LogEntity> listAllByIpLog(String ipLog, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        try {
            return new PageImpl<>(logJDBCRepository
                    .findAllByIpContains(ipLog, chkPageable(page, linesPerPage, orderBy, direction))
                    .stream()
                    .limit(chkLimited(limited))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Page.empty();
        }
    }

    @Override
    public Optional<LogEntity> findByIdLog(String idLog) {
        return logJDBCRepository.findById(idLog);
    }

    @Override
    public Boolean deleteLog(String idLog) {
        try {
            logJDBCRepository.deleteById(idLog);
            return logJDBCRepository.findById(idLog).isEmpty();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Long countLogsByRequest(String request) {
        try {
            return (long) logJDBCRepository.findAllByActiveAndRequestContains(true, request).size();
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0L;
        }
    }
}