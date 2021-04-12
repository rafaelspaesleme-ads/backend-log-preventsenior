package br.com.preventsr.logs.services.impl;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;
import br.com.preventsr.logs.services.cli.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.preventsr.logs.utils.functions.ConvertionFunctions.*;
import static br.com.preventsr.logs.utils.functions.FileFunctions.*;
import static br.com.preventsr.logs.utils.functions.MessagesFunctions.customMsgSuccess;
import static org.springframework.http.HttpStatus.*;

@Service
public class LogServiceImpl implements LogService {

    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);
    private static final String SPLIT_LOG = Pattern.quote("|");

    private final LogDAO logDAO;

    public LogServiceImpl(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    @Override
    public ResponseDTO bulkInsertLog(FileLogDTO fileLogDTO) {
        try {
            List<LogEntity> logEntities = new ArrayList<>();
            AtomicReference<Boolean> save = new AtomicReference<>(false);

            File file = convertMultiPartToFile(fileLogDTO.getFile());

            BufferedReader dataFiles = new BufferedReader(new FileReader(file));

            String line;

            while ((line = dataFiles.readLine()) != null) {
                logEntities.add(LogEntity.builder()
                        .withFileName(file.getName())
                        .withDateTime(convertStringInLocalDateTime(line.split(SPLIT_LOG)[0]))
                        .withIp(line.split(SPLIT_LOG)[1])
                        .withRequest(line.split(SPLIT_LOG)[2].replaceAll(Pattern.quote("\""), ""))
                        .withStatusHttp(line.split(SPLIT_LOG)[3])
                        .withUserAgent(line.split(SPLIT_LOG)[4].replaceAll(Pattern.quote("\""), ""))
                        .withActive(true)
                        .build());
            }

            //2020-01-01 00:00:11.763|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"

            save.set(logDAO.bulkInsert(logEntities));

            return save.get()
                    ? ResponseDTO.builder()
                    .withData("Quantidade de dados adicionados: ".concat(String.valueOf(logEntities.size())))
                    .withError(null)
                    .withDateResponse(LocalDateTime.now())
                    .withMessage("Logs em massa cadastrados com sucesso!")
                    .withStatusHttp(CREATED.value())
                    .build()
                    : ResponseDTO.builder()
                    .withData(null)
                    .withError(NOT_IMPLEMENTED.getReasonPhrase())
                    .withDateResponse(LocalDateTime.now())
                    .withMessage("Não foi possivel cadastrar logs em massa, pois a lista esta vazia.")
                    .withStatusHttp(NOT_IMPLEMENTED.value())
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return ResponseDTO.builder()
                    .withData(null)
                    .withError(e.getCause())
                    .withDateResponse(LocalDateTime.now())
                    .withMessage("Lista vazia.")
                    .withStatusHttp(FORBIDDEN.value())
                    .build();
        }

    }

    @Override
    public ResponseDTO saveOrUpdateLog(LogDTO logDTO) {

        Boolean save = logDAO.insertOrEditLogDefault(LogEntity.builder()
                .withId(logDTO.getId())
                .withFileName(logDTO.getFileName())
                .withDateTime(logDTO.getDateTime())
                .withIp(logDTO.getIp())
                .withRequest(logDTO.getRequest())
                .withStatusHttp(logDTO.getStatusHttp())
                .withUserAgent(logDTO.getUserAgent())
                .withActive(logDTO.getActive())
                .build());

        return save
                ? ResponseDTO.builder()
                .withData(logDTO.getId())
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage(customMsgSuccess("Log", logDTO.getId() == null ? "cadastrado" : "atualizado"))
                .withStatusHttp(logDTO.getId() == null ? CREATED.value() : OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(logDTO.getId() == null ? NOT_IMPLEMENTED.getReasonPhrase() : NOT_MODIFIED.getReasonPhrase())
                .withDateResponse(LocalDateTime.now())
                .withMessage("Não foi possivel cadastrar logs em massa, pois a lista esta vazia.")
                .withStatusHttp(logDTO.getId() == null ? NOT_IMPLEMENTED.value() : NOT_MODIFIED.value())
                .build();
    }

    @Override
    public ResponseDTO listLogActive(Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByActiveLog(page, linesPerPage, orderBy, direction, limited)
                .forEach(logEntity -> {
                    logEntitieLogDTOList.add(LogDTO.builder()
                            .withId(logEntity.getId())
                            .withFileName(logEntity.getFileName())
                            .withDateTime(logEntity.getDateTime())
                            .withIp(logEntity.getIp())
                            .withRequest(logEntity.getRequest())
                            .withStatusHttp(logEntity.getStatusHttp())
                            .withUserAgent(logEntity.getUserAgent())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(new PageImpl<>(logEntitieLogDTOList))
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage("Lista de logs retornado com sucesso!")
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now())
                .withMessage("Não há logs cadastrados.")
                .withStatusHttp(NO_CONTENT.value())
                .build();
    }

    @Override
    public ResponseDTO listLogByNameContains(String name, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByNameLog(name, page, linesPerPage, orderBy, direction, limited)
                .stream()
                .filter(LogEntity::getActive)
                .collect(Collectors.toList())
                .forEach(logEntity -> {
                    logEntitieLogDTOList.add(LogDTO.builder()
                            .withId(logEntity.getId())
                            .withFileName(logEntity.getFileName())
                            .withDateTime(logEntity.getDateTime())
                            .withIp(logEntity.getIp())
                            .withRequest(logEntity.getRequest())
                            .withStatusHttp(logEntity.getStatusHttp())
                            .withUserAgent(logEntity.getUserAgent())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(new PageImpl<>(logEntitieLogDTOList))
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage("Consulta de logs do nome " + name + " retornado com sucesso!")
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now())
                .withMessage("Não há logs cadastrados para o nome " + name + ".")
                .withStatusHttp(NO_CONTENT.value())
                .build();
    }

    @Override
    public ResponseDTO listLogByIpContains(String ip, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByIpLog(ip, page, linesPerPage, orderBy, direction, limited)
                .stream()
                .filter(LogEntity::getActive)
                .collect(Collectors.toList())
                .forEach(logEntity -> {
                    logEntitieLogDTOList.add(LogDTO.builder()
                            .withId(logEntity.getId())
                            .withFileName(logEntity.getFileName())
                            .withDateTime(logEntity.getDateTime())
                            .withIp(logEntity.getIp())
                            .withRequest(logEntity.getRequest())
                            .withStatusHttp(logEntity.getStatusHttp())
                            .withUserAgent(logEntity.getUserAgent())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(new PageImpl<>(logEntitieLogDTOList))
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage("Consulta de logs na faixa de IP " + ip + " retornado com sucesso!")
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now())
                .withMessage("Não há logs cadastrados para o IP " + ip + ".")
                .withStatusHttp(NO_CONTENT.value())
                .build();
    }

    @Override
    public ResponseDTO findById(String id) {
        Optional<LogEntity> log = logDAO.findByIdLog(id);

        return log.isPresent()
                ? ResponseDTO.builder()
                .withData(LogDTO.builder()
                        .withId(log.get().getId())
                        .withFileName(log.get().getFileName())
                        .withDateTime(log.get().getDateTime())
                        .withIp(log.get().getIp())
                        .withRequest(log.get().getRequest())
                        .withStatusHttp(log.get().getStatusHttp())
                        .withUserAgent(log.get().getUserAgent())
                        .withActive(log.get().getActive())
                        .build())
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage("item do ID" + id + " retornado com sucesso!")
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now())
                .withMessage("Não foi possivel retornar o item do ID " + id + ".")
                .withStatusHttp(NO_CONTENT.value())
                .build();

    }

    @Override
    public ResponseDTO deleteById(String id) {
        Boolean delete = logDAO.deleteLog(id);

        return delete
                ? ResponseDTO.builder()
                .withData(id)
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage("Item do ID " + id + " deletado com sucesso!")
                .withStatusHttp(ACCEPTED.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NOT_ACCEPTABLE.getReasonPhrase())
                .withDateResponse(LocalDateTime.now())
                .withMessage("Ação para deletar item do ID " + id + " não foi aceito.")
                .withStatusHttp(NOT_ACCEPTABLE.value())
                .build();
    }
}
