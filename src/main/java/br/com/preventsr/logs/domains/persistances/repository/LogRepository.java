package br.com.preventsr.logs.domains.persistances.repository;

import br.com.preventsr.logs.domains.entities.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, String> {
    Page<LogEntity> findAllByUserAgentContains(String userAgent, Pageable pageable);
    Page<LogEntity> findAllByIpContains(String ipLog, Pageable pageable);
    Page<LogEntity> findAllByActive(Boolean active, Pageable pageable);
}
