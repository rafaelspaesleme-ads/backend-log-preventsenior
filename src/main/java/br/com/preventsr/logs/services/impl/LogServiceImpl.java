package br.com.preventsr.logs.services.impl;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;
import br.com.preventsr.logs.services.cli.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            List<String> lines = new ArrayList<>();
            List<LogEntity> logEntities = new ArrayList<>();
            List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

            File file = convertMultiPartToFile(fileLogDTO.getMultipartFile());

            BufferedReader dataFiles = new BufferedReader(new FileReader(file));

            String line;

            while ((line = dataFiles.readLine()) != null) {
                lines.add(line);
            }

            //2020-01-01 00:00:11.763|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"

            IntStream.range(0, lines.size()).forEach(index -> {
                logEntities.get(index).setFileName(fileLogDTO.getNameFile().concat(convertDateInCode(LocalDateTime.now())));
                logEntities.get(index).setDateTime(convertStringInLocalDateTime(lines.get(index).split(SPLIT_LOG)[0]));
                logEntities.get(index).setIp(lines.get(index).split(SPLIT_LOG)[1]);
                logEntities.get(index).setRequest(lines.get(index).split(SPLIT_LOG)[2]);
                logEntities.get(index).setStatusHttp(lines.get(index).split(SPLIT_LOG)[3]);
                logEntities.get(index).setUserAgent(lines.get(index).split(SPLIT_LOG)[4].replaceAll(Pattern.quote("\""), ""));
                logEntities.get(index).setActive(true);
            });


            logDAO.bulkInsert(logEntities)
                    .stream()
                    .filter(LogEntity::getActive)
                    .collect(Collectors.toList())
                    .forEach(logEntity ->
                            logEntitieLogDTOList.add(LogDTO.builder()
                                    .withId(logEntity.getId())
                                    .withFileName(logEntity.getFileName())
                                    .withDateTime(logEntity.getDateTime())
                                    .withIp(logEntity.getIp())
                                    .withRequest(logEntity.getRequest())
                                    .withStatusHttp(logEntity.getStatusHttp())
                                    .withUserAgent(logEntity.getRequest())
                                    .withActive(logEntity.getActive())
                                    .build()));

            return logEntitieLogDTOList.size() > 0
                    ? ResponseDTO.builder()
                    .withData(logEntitieLogDTOList)
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
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        Boolean save = logDAO.insertOrEditLogDefault(LogEntity.builder()
                .withId(logDTO.getId())
                .withFileName(logDTO.getFileName())
                .withDateTime(logDTO.getDateTime())
                .withIp(logDTO.getIp())
                .withRequest(logDTO.getRequest())
                .withStatusHttp(logDTO.getStatusHttp())
                .withUserAgent(logDTO.getRequest())
                .withActive(logDTO.getActive())
                .build());

        if (save) {
            logDAO.listAllByActiveLog()
                    .forEach(logEntity -> {
                        logEntitieLogDTOList.add(LogDTO.builder()
                                .withId(logEntity.getId())
                                .withFileName(logEntity.getFileName())
                                .withDateTime(logEntity.getDateTime())
                                .withIp(logEntity.getIp())
                                .withRequest(logEntity.getRequest())
                                .withStatusHttp(logEntity.getStatusHttp())
                                .withUserAgent(logEntity.getRequest())
                                .withActive(logEntity.getActive())
                                .build());
                    });
        }

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(logEntitieLogDTOList)
                .withError(null)
                .withDateResponse(LocalDateTime.now())
                .withMessage(customMsgSuccess("Logs", logDTO.getId() == null ? "cadastrado" : "atualizado"))
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
    public ResponseDTO listLogActive() {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByActiveLog()
                .forEach(logEntity -> {
                    logEntitieLogDTOList.add(LogDTO.builder()
                            .withId(logEntity.getId())
                            .withFileName(logEntity.getFileName())
                            .withDateTime(logEntity.getDateTime())
                            .withIp(logEntity.getIp())
                            .withRequest(logEntity.getRequest())
                            .withStatusHttp(logEntity.getStatusHttp())
                            .withUserAgent(logEntity.getRequest())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(logEntitieLogDTOList)
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
    public ResponseDTO listLogByNameContains(String name) {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByNameLog(name)
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
                            .withUserAgent(logEntity.getRequest())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(logEntitieLogDTOList)
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
    public ResponseDTO listLogByIpContains(String ip) {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByIpLog(ip)
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
                            .withUserAgent(logEntity.getRequest())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return logEntitieLogDTOList.size() > 0
                ? ResponseDTO.builder()
                .withData(logEntitieLogDTOList)
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
                        .withUserAgent(log.get().getRequest())
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
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        Boolean delete = logDAO.deleteLog(id);

        logDAO.listAllByActiveLog()
                .forEach(logEntity -> {
                    logEntitieLogDTOList.add(LogDTO.builder()
                            .withId(logEntity.getId())
                            .withFileName(logEntity.getFileName())
                            .withDateTime(logEntity.getDateTime())
                            .withIp(logEntity.getIp())
                            .withRequest(logEntity.getRequest())
                            .withStatusHttp(logEntity.getStatusHttp())
                            .withUserAgent(logEntity.getRequest())
                            .withActive(logEntity.getActive())
                            .build());
                });

        return delete
                ? ResponseDTO.builder()
                .withData(logEntitieLogDTOList)
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
