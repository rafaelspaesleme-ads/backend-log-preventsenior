package br.com.preventsr.logs.resources.v1.controller;

import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;
import br.com.preventsr.logs.services.cli.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/logs")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @CrossOrigin
    @PostMapping(value = "/upload")
    public ResponseEntity<ResponseDTO> uploadLog(@ModelAttribute FileLogDTO fileLogDTO) {
        ResponseDTO responseDTO = logService.bulkInsertLog(fileLogDTO);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @PatchMapping(value = "/save-or-update")
    public ResponseEntity<ResponseDTO> saveOrUpdateLog(@RequestBody LogDTO logDTO) {
        ResponseDTO responseDTO = logService.saveOrUpdateLog(logDTO);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @GetMapping(value = "/list")
    public ResponseEntity<ResponseDTO> lists(
            @RequestParam(required = true, value = "page") Integer page,
            @RequestParam(required = true, value = "linesPerPage") Integer linesPerPage,
            @RequestParam(required = false, value = "orderBy") String orderBy,
            @RequestParam(required = false, value = "direction") String direction,
            @RequestParam(required = false, value = "limited") Long limited,
            @RequestParam(required = false, value = "userAgent") String userAgent,
            @RequestParam(required = false, value = "ip") String ip) {

        if (userAgent != null && ip == null) {
            ResponseDTO responseDTO = logService.listLogByUserAgentContains(userAgent, page, linesPerPage, orderBy, direction, limited);
            return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
        } else if (ip != null && userAgent == null) {
            ResponseDTO responseDTO = logService.listLogByIpContains(ip, page, linesPerPage, orderBy, direction, limited);
            return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
        } else {
            ResponseDTO responseDTO = logService.listLogActive(page, linesPerPage, orderBy, direction, limited);
            return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/select-by/{id}")
    public ResponseEntity<ResponseDTO> selectById(@PathVariable String id) {
        ResponseDTO responseDTO = logService.findById(id);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @DeleteMapping(value = "/delete-by/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@PathVariable String id) {
        ResponseDTO responseDTO = logService.deleteById(id);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }
}
