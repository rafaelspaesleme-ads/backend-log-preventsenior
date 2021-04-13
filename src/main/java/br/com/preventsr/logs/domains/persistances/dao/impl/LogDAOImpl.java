package br.com.preventsr.logs.domains.persistances.dao.impl;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.domains.persistances.repository.LogJDBCRepository;
import br.com.preventsr.logs.domains.persistances.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<LogEntity> logEntities = new ArrayList<>();
        logJDBCRepository.findAll().forEach(logEntities::add);
        return logEntities;
    }

    @Override
    public Page<LogEntity> listAllByActiveLog(Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        return new PageImpl<>(logJDBCRepository.findAllByActive(true, buildPageable(page, linesPerPage, orderBy, direction)).stream().limit(limited).collect(Collectors.toList()));
    }

    @Override
    public Page<LogEntity> listAllByUserAgentLog(String userAgent, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        return new PageImpl<>(logJDBCRepository.findAllByUserAgentContains(userAgent, buildPageable(page, linesPerPage, orderBy, direction)).stream().limit(limited).collect(Collectors.toList()));
    }

    @Override
    public Page<LogEntity> listAllByIpLog(String ipLog, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        return new PageImpl<>(logJDBCRepository.findAllByIpContains(ipLog, buildPageable(page, linesPerPage, orderBy, direction)).stream().limit(limited).collect(Collectors.toList()));
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
    public Long countLogsCustomHours(Long hours) {
        return (long) logRepository
                .findAllByActiveAndDateTimeBetween(true, LocalDateTime.now().minusHours(hours),
                        LocalDateTime.now())
                .size();
    }

    @Override
    public Long countLogsRequestCustomHours(String request, Long hours) {
        return (long) logRepository
                .findAllByActiveAndRequestContainsAndDateTimeBetween(true, request,
                        LocalDateTime.now().minusHours(hours),
                        LocalDateTime.now())
                .size();
    }

    private PageRequest buildPageable(Integer page, Integer linesPerPage, String orderBy, String direction) {
        return PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
    }
}