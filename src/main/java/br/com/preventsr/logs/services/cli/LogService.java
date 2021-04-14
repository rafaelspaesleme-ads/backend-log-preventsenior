package br.com.preventsr.logs.services.cli;

import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;

public interface LogService {
    ResponseDTO bulkInsertLog(FileLogDTO fileLogDTO);
    ResponseDTO saveOrUpdateLog(LogDTO logDTO);
    ResponseDTO listLogActive(Integer page, Integer linesPerPage, String orderBy, String direction, Long limited);
    ResponseDTO listLogByUserAgentContains(String userAgent, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited);
    ResponseDTO listLogByIpContains(String ip, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited);
    ResponseDTO findById(String id);
    ResponseDTO deleteById(String id);
    ResponseDTO countLogsByRequest(String request);
}
