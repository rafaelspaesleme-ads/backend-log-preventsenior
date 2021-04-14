package br.com.preventsr.logs.resources.v1.controller;

import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;
import br.com.preventsr.logs.services.cli.LogService;
import br.com.preventsr.logs.utils.functions.TemplatesFunction;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/logs")
@Api(value = "Logs Controller", description = "REST API de Logs", tags = {"Logs"})
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @CrossOrigin
    @PostMapping(value = "/upload", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    @ApiOperation(value = "Upload de arquivo de Log", tags = {"Logs"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Dados enviados na requisição cadastrado com sucesso!"),
            @ApiResponse(code = 404, message = "Arquivo enviado na requisição não funciona ou esta invalido."),
            @ApiResponse(code = 501, message = "Não foi possivel cadastrar dados enviados na requisição."),
    })
    public ResponseEntity<ResponseDTO> uploadLog(@RequestPart(value = "file") MultipartFile file) {
        ResponseDTO responseDTO = logService.bulkInsertLog(FileLogDTO.builder().withFile(file).withSizeFile(file.getSize()).build());
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @PatchMapping(value = "/save-or-update")
    @ApiOperation(value = "Salvar ou Atualizar Logs", tags = {"Logs"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sucesso no envio ou retorno de requisição."),
            @ApiResponse(code = 201, message = "Dados enviados na requisição cadastrado com sucesso!"),
            @ApiResponse(code = 304, message = "Não foi possivel atualizar dados enviados na requisição."),
            @ApiResponse(code = 501, message = "Não foi possivel cadastrar dados enviados na requisição."),
    })
    public ResponseEntity<ResponseDTO> saveOrUpdateLog(@RequestBody LogDTO logDTO) {
        ResponseDTO responseDTO = logService.saveOrUpdateLog(logDTO);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @GetMapping(value = "/list")
    @ApiOperation(value = "Tipos de listagem de Logs", tags = {"Logs"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sucesso no envio ou retorno de requisição."),
            @ApiResponse(code = 204, message = "Não há conteudo para ser retornado na resposta da requisição."),
    })
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
    @GetMapping(value = "/count")
    @ApiOperation(value = "Quantidade de Verbos HTTP", tags = {"Logs"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sucesso no envio ou retorno de requisição."),
            @ApiResponse(code = 500, message = "Não foi possivel retornar dados do servidor."),
    })
    public ResponseEntity<ResponseDTO> countLogsByRequest(@RequestParam(value = "request") String request) {
        ResponseDTO responseDTO = logService.countLogsByRequest(request);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @GetMapping(value = "/select-by/{id}")
    @ApiOperation(value = "Selecionar por ID de Log", tags = {"Logs"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sucesso no envio ou retorno de requisição."),
            @ApiResponse(code = 204, message = "Não há conteudo para ser retornado na resposta da requisição."),
    })
    public ResponseEntity<ResponseDTO> selectById(@PathVariable String id) {
        ResponseDTO responseDTO = logService.findById(id);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }

    @CrossOrigin
    @DeleteMapping(value = "/delete-by/{id}")
    @ApiOperation(value = "Deletar por ID de Log", tags = {"Logs"})
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Dado enviado na requisição foi aceito para exclusão."),
            @ApiResponse(code = 406, message = "Não foi aceito para exclusão, o dado enviado na requisição.")
    })
    public ResponseEntity<ResponseDTO> deleteById(@PathVariable String id) {
        ResponseDTO responseDTO = logService.deleteById(id);
        return ResponseEntity.status(responseDTO.getStatusHttp()).body(responseDTO);
    }
}
