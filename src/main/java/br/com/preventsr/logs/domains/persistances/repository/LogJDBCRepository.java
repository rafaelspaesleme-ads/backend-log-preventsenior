package br.com.preventsr.logs.domains.persistances.repository;

import br.com.preventsr.logs.domains.entities.LogEntity;
import org.springframework.data.repository.CrudRepository;

public interface LogJDBCRepository extends CrudRepository<LogEntity, String> {
    Iterable<LogEntity> findAllByActive(Boolean active);
    Iterable<LogEntity> findAllByFileNameContains(String nameLog);
    Iterable<LogEntity> findAllByIpContains(String ipLog);
}
