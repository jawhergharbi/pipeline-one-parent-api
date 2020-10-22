package com.sawoo.pipeline.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/downloads")
public class FileDownloadControllerTest {

    private final ResourceLoader resourceLoader;

    @RequestMapping(
            value = "/json/byte",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> getJsonByteArray() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "json"));
            headers.setContentDisposition(ContentDisposition.builder("inline; filename=file.json").build());
            Resource resource = resourceLoader.getResource("classpath:downloads/file.json");
            byte[] targetArray = new byte[resource.getInputStream().available()];
            resource.getInputStream().read(targetArray);
            return ResponseEntity
                    .ok()
                    .contentLength(resource.contentLength())
                    .headers(headers)
                    .body( targetArray );

        } catch (IOException err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @RequestMapping(
            value = "/json/stream",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<InputStreamResource> getJsonStream() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "json"));
            headers.setContentDisposition(ContentDisposition.builder("inline; filename=file.json").build());
            Resource resource = resourceLoader.getResource("classpath:downloads/file.json");
            return ResponseEntity
                    .ok()
                    .contentLength(resource.contentLength())
                    .headers(headers)
                    .body( new InputStreamResource(resource.getInputStream()) );

        } catch (IOException err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @RequestMapping(
            value = "/text",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<InputStreamResource> getText() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "plain"));
            headers.setContentDisposition(ContentDisposition.builder("inline; filename=file.txt").build());
            Resource resource = resourceLoader.getResource("classpath:downloads/file.txt");
            return ResponseEntity
                    .ok()
                    .contentLength(resource.contentLength())
                    .headers(headers)
                    .body( new InputStreamResource(resource.getInputStream()) );

        } catch (IOException err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @RequestMapping(
            value = "/csv",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<InputStreamResource> getCSV() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "csv"));
            headers.setContentDisposition(ContentDisposition.builder("inline; filename=file.csv").build());
            Resource resource = resourceLoader.getResource("classpath:downloads/file.csv");
            return ResponseEntity
                    .ok()
                    .contentLength(resource.contentLength())
                    .headers(headers)
                    .body( new InputStreamResource(resource.getInputStream()) );

        } catch (IOException err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @RequestMapping(
            value = "/pdf",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<InputStreamResource> getPDF() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment; filename=file.pdf").build());
            Resource resource = resourceLoader.getResource("classpath:downloads/file.pdf");
            File file = new File("./file.pdf");
            byte[] targetArray = Files.readAllBytes(file.toPath());
            System.out.printf("Target array length: %d", targetArray.length);
            targetArray = getClass().getClassLoader().getResourceAsStream("./file.pdf").readAllBytes(); //resource.getInputStream().readAllBytes();
            System.out.printf("Target array length: %d", targetArray.length);
            return ResponseEntity
                    .ok()
                    .contentLength(targetArray.length)
                    .headers(headers)
                    .body( new InputStreamResource(new ByteArrayInputStream(targetArray)) );

        } catch (IOException err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @RequestMapping(
            value = "/image",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<InputStreamResource> getImage() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDisposition(ContentDisposition.builder("attachment; filename=file.png").build());
            Resource resource = resourceLoader.getResource("classpath:downloads/file.png");
            return ResponseEntity
                    .ok()
                    .contentLength(resource.contentLength())
                    .headers(headers)
                    .body( new InputStreamResource(resource.getInputStream()) );

        } catch (IOException err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
