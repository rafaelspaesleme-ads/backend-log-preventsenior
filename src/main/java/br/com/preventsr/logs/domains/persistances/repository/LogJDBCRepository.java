package br.com.preventsr.logs.domains.persistances.repository;

import br.com.preventsr.logs.domains.entities.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogJDBCRepository extends CrudRepository<LogEntity, String> {
    Page<LogEntity> findAllByUserAgentContains(String userAgent, Pageable pageable);
    Page<LogEntity> findAllByIpContains(String ipLog, Pageable pageable);
    Page<LogEntity> findAllByActive(Boolean active, Pageable pageable);
    List<LogEntity> findAllByActiveAndRequestContains(Boolean active, String request);
}
