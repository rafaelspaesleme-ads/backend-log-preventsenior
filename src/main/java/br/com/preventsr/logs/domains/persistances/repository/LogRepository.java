package br.com.preventsr.logs.domains.persistances.repository;

import br.com.preventsr.logs.domains.entities.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, String> {
    Page<LogEntity> findAllByUserAgentContains(String userAgent, Pageable pageable);
    Page<LogEntity> findAllByIpContains(String ipLog, Pageable pageable);
    Page<LogEntity> findAllByActive(Boolean active, Pageable pageable);
    List<LogEntity> findAllByActiveAndDateTimeBetween(Boolean active, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd);
    List<LogEntity> findAllByActiveAndRequestContainsAndDateTimeBetween(Boolean active, String request, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd);
}
