package br.com.preventsr.logs.services.cli;

import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;

public interface LogService {
    ResponseDTO bulkInsertLog(FileLogDTO fileLogDTO);
    ResponseDTO saveOrUpdateLog(LogDTO logDTO);
    ResponseDTO listLogActive();
    ResponseDTO listLogByNameContains(String name);
    ResponseDTO listLogByIpContains(String ip);
    ResponseDTO findById(String id);
    ResponseDTO deleteById(String id);

}
