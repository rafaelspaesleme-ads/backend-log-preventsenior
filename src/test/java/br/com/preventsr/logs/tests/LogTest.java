package br.com.preventsr.logs.tests;

import br.com.preventsr.logs.domains.entities.LogEntity;
import br.com.preventsr.logs.domains.persistances.dao.cli.LogDAO;
import br.com.preventsr.logs.domains.persistances.repository.LogRepository;
import br.com.preventsr.logs.resources.v1.dto.FileLogDTO;
import br.com.preventsr.logs.resources.v1.dto.LogDTO;
import br.com.preventsr.logs.resources.v1.dto.ResponseDTO;
import br.com.preventsr.logs.services.cli.LogService;
import com.github.javafaker.Faker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LogTest {
    @Autowired
    private LogService logService;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private LogDAO logDAO;

    @Test
    public void uploadLog() throws IOException {
        String name = "access_test.log";
        String originalFileName = "access_test.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        fw.write("2020-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"");
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        ResponseDTO responseDTO = logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Assert.assertEquals("Era para retornar status 201", 201, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void burkUploadLog() throws IOException {
        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 100000; i++) {
            fw.write("2020-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        ResponseDTO responseDTO = logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Assert.assertEquals("Era para retornar status 201", 201, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void burkUploadNotFound() throws IOException {
        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write("2020-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        }
        fw.write("teste----\n");
        for (int i = 0; i <= 5000; i++) {
            fw.write("2020-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        ResponseDTO responseDTO = logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        System.out.println("status: " + responseDTO.getStatusHttp());

        Assert.assertEquals("Era para retornar status 404", 404, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void burkUploadNotImplement() throws IOException {
        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        ResponseDTO responseDTO = logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        System.out.println("status: " + responseDTO.getStatusHttp());

        Assert.assertEquals("Era para retornar status 501", 501, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void save() {
        ResponseDTO responseDTO = logService.saveOrUpdateLog(LogDTO.builder()
                .withId(null)
                .withActive(true)
                .withDateTime(LocalDateTime.now())
                .withFileName("acesso.log")
                .withIp("192.168.0.86")
                .withRequest("GET")
                .withStatusHttp("200")
                .withUserAgent("Mozilla Firefox")
                .build());

        Assert.assertEquals("Era para retornar status 201", 201, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void update() {

        LogEntity logEntity = logRepository.save(LogEntity.builder()
                .withId(null)
                .withActive(true)
                .withDateTime(LocalDateTime.now())
                .withFileName("acesso.log")
                .withIp("192.168.0.86")
                .withRequest("GET")
                .withStatusHttp("200")
                .withUserAgent("Mozilla Firefox")
                .build());

        ResponseDTO responseDTO = logService.saveOrUpdateLog(LogDTO.builder()
                .withId(logEntity.getId())
                .withActive(true)
                .withDateTime(LocalDateTime.now())
                .withFileName("acesso.log")
                .withIp("192.168.0.205")
                .withRequest("GET")
                .withStatusHttp("204")
                .withUserAgent("Mozilla Firefox")
                .build());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void notUpdate() {
        LocalDateTime now = LocalDateTime.now();

        LogEntity logEntity = logRepository.save(LogEntity.builder()
                .withId(null)
                .withActive(true)
                .withDateTime(now)
                .withFileName("acesso.log")
                .withIp("192.168.0.86")
                .withRequest("GET")
                .withStatusHttp("200")
                .withUserAgent("Mozilla Firefox")
                .build());

        ResponseDTO responseDTO = logService.saveOrUpdateLog(LogDTO.builder()
                .withId(logEntity.getId())
                .withActive(true)
                .withDateTime(now)
                .withFileName("acesso.log")
                .withIp("192.168.0.86")
                .withRequest("GET")
                .withStatusHttp("200")
                .withUserAgent("Mozilla Firefox")
                .build());

        Assert.assertEquals("Era para retornar status 304", 304, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void notSave() {

        ResponseDTO responseDTO = logService.saveOrUpdateLog(null);

        Assert.assertEquals("Era para retornar status 501", 501, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPagination0LinesPage10orderByDateTImeDirDESC() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|192.168.234.82|\"GET / HTTP/1.1\"|200|\"" + i + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByActiveLog(0, 10, "dateTime", "DESC", 1000L);

        ResponseDTO responseDTO = logService.listLogActive(0, 10, "dateTime", "DESC", 1000L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Primeiro item da lista tem que conter 5000", "5000", logEntities.getContent().get(0).getUserAgent());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPagination1LinesPage10orderByDateTImeDirASC() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|192.168.234.82|\"GET / HTTP/1.1\"|200|\"" + i + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByActiveLog(1, 10, "dateTime", "ASC", 1000L);

        ResponseDTO responseDTO = logService.listLogActive(1, 10, "dateTime", "ASC", 1000L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Primeiro item da lista tem que conter 10", "10", logEntities.getContent().get(0).getUserAgent());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listNotContent() {

        ResponseDTO responseDTO = logService.listLogActive(1, 10, "dateTime", "ASC", 1000L);

        Assert.assertEquals("Era para retornar status 204", 204, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPaginationUserAgent0LinesPage10orderByDateTImeDirDESC() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|192.168.234.82|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByUserAgentLog("João 15", 0, 10, "dateTime", "DESC", 1000L);

        ResponseDTO responseDTO = logService.listLogByUserAgentContains("João 15", 0, 10, "dateTime", "DESC", 1000L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Primeiro item da lista tem que conter João 150005", "João 150005", logEntities.getContent().get(0).getUserAgent());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPaginationUserAgent1LinesPage10orderByDateTImeDirASC() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|192.168.234.82|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByUserAgentLog("João 15", 1, 10, "dateTime", "ASC", 1000L);

        ResponseDTO responseDTO = logService.listLogByUserAgentContains("João 15", 1, 10, "dateTime", "ASC", 1000L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Primeiro item da lista tem que conter João 1595", "João 1595", logEntities.getContent().get(0).getUserAgent());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listUserAgentNotContent() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|192.168.234.82|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        ResponseDTO responseDTO = logService.listLogByUserAgentContains("Joao 1595", 1, 10, "dateTime", "ASC", 1000L);

        Assert.assertEquals("Era para retornar status 204", 204, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPaginationIp0LinesPage10orderByDateTIme() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByIpLog("192.168.1", 0, 10, "dateTime", "DESC", 1000L);

        ResponseDTO responseDTO = logService.listLogByIpContains("192.168.1", 0, 10, "dateTime", "DESC", 1000L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPaginationIp1LinesPage10orderByDateTIme() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 15000; i++) {
            fw.write(LocalDateTime
                    .now()
                    .toString()
                    .replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByIpLog("192.168.1", 1, 10, "dateTime", "DESC", 1000L);

        ResponseDTO responseDTO = logService.listLogByIpContains("192.168.1", 1, 10, "dateTime", "DESC", 1000L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPaginationIp2LinesPage10orderByDateTImeDirASC() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 255; i++) {
            fw.write(LocalDateTime
                    .now()
                    .toString()
                    .replace("T", " ") + "|192.168.12." + i + "|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        //file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByIpLog("192.168.1", 2, 10, "dateTime", "ASC", 255L);

        ResponseDTO responseDTO = logService.listLogByIpContains("192.168.1", 2, 10, "dateTime", "ASC", 255L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Lista tem que retornar o IP 192.168.12.20", "192.168.12.20", logEntities.getContent().get(0).getIp());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listPaginationIp2LinesPage10orderByDateTImeDirDESC() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 255; i++) {
            fw.write(LocalDateTime
                    .now()
                    .toString()
                    .replace("T", " ") + "|192.168.12." + i + "|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByIpLog("192.168.1", 2, 10, "dateTime", "DESC", 255L);

        ResponseDTO responseDTO = logService.listLogByIpContains("192.168.1", 2, 10, "dateTime", "DESC", 255L);

        Assert.assertEquals("Lista tem que retornar 10", 10, logEntities.getTotalElements());

        Assert.assertEquals("Lista tem que retornar o IP 192.168.12.235", "192.168.12.235", logEntities.getContent().get(0).getIp());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void listUserIpContent() throws IOException {

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 255; i++) {
            fw.write(LocalDateTime
                    .now()
                    .toString()
                    .replace("T", " ") + "|192.168.12." + i + "|\"GET / HTTP/1.1\"|200|\"João 1" + i + "5\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Page<LogEntity> logEntities = logDAO.listAllByIpLog("192.168.2", 2, 10, "dateTime", "DESC", 255L);

        ResponseDTO responseDTO = logService.listLogByIpContains("192.168.2", 2, 10, "dateTime", "DESC", 255L);

        Assert.assertEquals("Era para retornar status 204", 204, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void findById() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"" + faker.starTrek() + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        List<LogEntity> logEntities = logDAO.listAllLog();

        Optional<LogEntity> log = logDAO.findByIdLog(logEntities.get(0).getId());

        ResponseDTO responseDTO = logService.findById(logEntities.get(0).getId());

        Assert.assertTrue("Era para retornar um item", log.isPresent());

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void findByIdNotContent() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"" + faker.starTrek() + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        List<LogEntity> logEntities = logDAO.listAllLog();

        String id = logEntities.get(0).getId();

        logDAO.deleteLog(id);

        Optional<LogEntity> log = logDAO.findByIdLog(id);
        ResponseDTO responseDTO = logService.findById(id);

        Assert.assertTrue("Era para retornar item vazio", log.isEmpty());
        Assert.assertEquals("Era para retornar status 204", 204, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void deleteById() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"" + faker.starTrek() + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        List<LogEntity> logEntities = logDAO.listAllLog();

        ResponseDTO responseDTO = logService.deleteById(logEntities.get(0).getId());

        Boolean delete = logDAO.deleteLog(logEntities.get(30).getId());

        Assert.assertTrue("Era para retornar VERDADEIRO", delete);
        Assert.assertEquals("Era para retornar status 202", 202, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void deleteByIdNotAcceptable() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"" + faker.starTrek() + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Boolean delete = logDAO.deleteLog("11369ac7-7f50-45f6-8ffb-44c&c7887b");

        ResponseDTO responseDTO = logService.deleteById("11369ac7-7f50-45f6-8ffb-44c&c788/b");

        Assert.assertEquals("Era para retornar FALSO", false, delete);
        Assert.assertEquals("Era para retornar status 406", 406, (int) responseDTO.getStatusHttp());
    }

    @Test
    public void countRequest() throws IOException {
        Faker faker = new Faker();

        String name = "access_test_2.log";
        String originalFileName = "access_test_2.log";
        String contentType = "text/plain";
        byte[] content = null;
        File file = new File("access_test_2.log");
        file.createNewFile();
        Writer fw = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i <= 5000; i++) {
            fw.write(LocalDateTime.now().toString().replace("T", " ") + "|" + faker
                    .number()
                    .numberBetween(192, 192) + "." + faker
                    .number()
                    .numberBetween(168, 168) + "." + faker
                    .number()
                    .numberBetween(0, 255) + "." + faker
                    .number()
                    .numberBetween(1, 255) + "|\"GET / HTTP/1.1\"|200|\"" + faker.starTrek() + "\"\n");
        }
        fw.close();

        content = Files.readAllBytes(file.toPath());

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        logService.bulkInsertLog(FileLogDTO.builder().withSizeFile(result.getSize()).withFile(result).build());

        file.delete();

        Long log = logDAO.countLogsByRequest("GET");
        Long logPOST = logDAO.countLogsByRequest("POST");

        ResponseDTO responseDTO = logService.countLogsByRequest("GET");

        Assert.assertEquals("Era para retornar 5001", 5001, (long) log);
        Assert.assertEquals("Era para retornar 0", 0, (long) logPOST);

        Assert.assertEquals("Era para retornar status 200", 200, (int) responseDTO.getStatusHttp());
    }
}
