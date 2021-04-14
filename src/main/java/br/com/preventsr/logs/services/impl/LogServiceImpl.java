package br.com.preventsr.logs.services.impl;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;
import br.com.preventsr.logs.services.cli.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.preventsr.logs.utils.functions.ValidatorsFunctions.*;
import static br.com.preventsr.logs.utils.functions.ConvertionFunctions.*;
import static br.com.preventsr.logs.utils.functions.FileFunctions.*;
import static br.com.preventsr.logs.utils.functions.MessagesFunctions.*;
import static br.com.preventsr.logs.utils.functions.MessagesFunctions.msgConsult;
import static org.springframework.http.HttpStatus.*;

@PropertySource(value = "classpath:messages/messages.properties", encoding = "UTF-8")
@Service
public class LogServiceImpl implements LogService {

    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);
    private static final String SPLIT_LOG = Pattern.quote("|");
    private static final String ZONE_INFO = "America/Sao_Paulo";

    @Value("${message.post.invalidated.inside.file}")
    private String msgInvalidatedInsideFile;

    @Value("${message.get.list.empty}")
    private String msgListEmpty;

    @Value("${message.post.bulk.insert.success}")
    private String msgBulkInsertSuccess;

    @Value("${message.post.bulk.insert.fail.list.empty}")
    private String msgBulkInsertFailListEmpty;

    @Value("${message.patch.nothing.modify}")
    private String msgNothingModify;

    @Value("${message.patch.bad.request}")
    private String msgBadRequest;

    @Value("${message.patch.bad.status.http}")
    private String msgBadStatusHttp;

    @Value("${message.patch.not.save}")
    private String msgNotSave;

    @Value("${message.get.list.success}")
    private String msgListSuccess;

    @Value("${message.get.list.not.content}")
    private String msgListNotContent;

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
                String[] split = line.split(SPLIT_LOG);
                if (split.length == 5) {
                    logEntities.add(LogEntity.builder()
                            .withFileName(file.getName())
                            .withDateTime(convertStringInLocalDateTime(line.split(SPLIT_LOG)[0]))
                            .withIp(line.split(SPLIT_LOG)[1])
                            .withRequest(line.split(SPLIT_LOG)[2].replaceAll(Pattern.quote("\""), ""))
                            .withStatusHttp(line.split(SPLIT_LOG)[3])
                            .withUserAgent(line.split(SPLIT_LOG)[4].replaceAll(Pattern.quote("\""), ""))
                            .withActive(true)
                            .build());
                } else {
                    return ResponseDTO.builder()
                            .withData(null)
                            .withError(msgInvalidatedInsideFile)
                            .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                            .withMessage(msgListEmpty)
                            .withStatusHttp(NOT_FOUND.value())
                            .build();
                }
            }

            if (logEntities.size() > 0) {
                save.set(logDAO.bulkInsert(logEntities));
            } else {
                save.set(false);
            }

            file.delete();

            return save.get()
                    ? ResponseDTO.builder()
                    .withData(msgCountDataAdd(String.valueOf(logEntities.size())))
                    .withError(null)
                    .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                    .withMessage(msgBulkInsertSuccess)
                    .withStatusHttp(CREATED.value())
                    .build()
                    : ResponseDTO.builder()
                    .withData(null)
                    .withError(NOT_IMPLEMENTED.getReasonPhrase())
                    .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                    .withMessage(msgBulkInsertFailListEmpty)
                    .withStatusHttp(NOT_IMPLEMENTED.value())
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return ResponseDTO.builder()
                    .withData(null)
                    .withError(e.getCause())
                    .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                    .withMessage(msgListEmpty)
                    .withStatusHttp(NOT_FOUND.value())
                    .build();
        }

    }

    @Override
    public ResponseDTO saveOrUpdateLog(LogDTO logDTO) {
        Boolean save = false;

        if (logDTO != null) {
            if (!chkRequest(logDTO.getRequest())) {
                return ResponseDTO.builder()
                        .withData(null)
                        .withError(BAD_REQUEST.getReasonPhrase())
                        .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                        .withMessage(msgBadRequest)
                        .withStatusHttp(BAD_REQUEST.value())
                        .build();
            }
            if (!chkStatusHttp(Integer.parseInt(logDTO.getStatusHttp()))) {
                return ResponseDTO.builder()
                        .withData(null)
                        .withError(BAD_REQUEST.getReasonPhrase())
                        .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                        .withMessage(msgBadStatusHttp)
                        .withStatusHttp(BAD_REQUEST.value())
                        .build();
            }

            if (logDTO.getId() != null) {
                AtomicReference<Boolean> update = new AtomicReference<>(false);
                logDAO.findByIdLog(logDTO.getId()).ifPresent(logEntity -> {
                    update.set(chkUpdate(logDTO, logEntity));
                });

                if (!update.get()) {
                    return ResponseDTO.builder()
                            .withData(null)
                            .withError(NOT_MODIFIED.getReasonPhrase())
                            .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                            .withMessage(msgNothingModify)
                            .withStatusHttp(NOT_MODIFIED.value())
                            .build();
                }
            }

            save = logDAO.insertOrEditLogDefault(LogEntity.builder()
                    .withId(logDTO.getId())
                    .withFileName(logDTO.getFileName())
                    .withDateTime(logDTO.getDateTime())
                    .withIp(logDTO.getIp())
                    .withRequest(logDTO.getRequest())
                    .withStatusHttp(logDTO.getStatusHttp())
                    .withUserAgent(logDTO.getUserAgent())
                    .withActive(logDTO.getActive())
                    .build());
        }

        return save
                ? ResponseDTO.builder()
                .withData(logDTO.getId())
                .withError(null)
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(customMsgSuccess("Log", logDTO.getId() == null ? "cadastrado" : "atualizado"))
                .withStatusHttp(logDTO.getId() == null ? CREATED.value() : OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NOT_IMPLEMENTED.getReasonPhrase())
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgNotSave)
                .withStatusHttp(NOT_IMPLEMENTED.value())
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
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgListSuccess)
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgListNotContent)
                .withStatusHttp(NO_CONTENT.value())
                .build();
    }

    @Override
    public ResponseDTO listLogByUserAgentContains(String userAgent, Integer page, Integer linesPerPage, String orderBy, String direction, Long limited) {
        List<LogDTO> logEntitieLogDTOList = new ArrayList<>();

        logDAO.listAllByUserAgentLog(userAgent, page, linesPerPage, orderBy, direction, limited)
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
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgConsult("User Agent", userAgent, true))
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgConsult("User Agent", userAgent, false))
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
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgConsult("IP", ip, true))
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgConsult("IP", ip, false))
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
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgConsult("ID", id, true))
                .withStatusHttp(OK.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NO_CONTENT.getReasonPhrase())
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgConsult("ID", id, false))
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
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgDelete("ID", id, true))
                .withStatusHttp(ACCEPTED.value())
                .build()
                : ResponseDTO.builder()
                .withData(null)
                .withError(NOT_ACCEPTABLE.getReasonPhrase())
                .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                .withMessage(msgDelete("ID", id, false))
                .withStatusHttp(NOT_ACCEPTABLE.value())
                .build();
    }

    @Override
    public ResponseDTO countLogsByRequest(String request) {
        try {
            Long countLog = logDAO.countLogsByRequest(request);
            return ResponseDTO.builder()
                    .withData(countLog)
                    .withError(null)
                    .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                    .withMessage(msgCounts("verbo http", request, countLog, true))
                    .withStatusHttp(OK.value())
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .withData(null)
                    .withError(e.getMessage())
                    .withDateResponse(LocalDateTime.now(ZoneId.of(ZONE_INFO)))
                    .withMessage(msgCounts("verbo http", request, 0, false))
                    .withStatusHttp(INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }
}
