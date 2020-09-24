package com.sawoo.pipeline.api.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/common")
public class DummyController {

    private final MessageSource messageSource;
    private final DummyService dummyService;

    @Value("${java.version}")
    private String javaVersion;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${app.server}")
    private String server;

    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public String hello() {
        return "<h3>Hello, here I am!!!</h3>" +
                "<p>" +
                    "<span>" +
                        "<b>java.version</b> ::" + javaVersion +
                    "</span>" +
                "</p>" +
                "<p>" +
                    "<span>" +
                        "<b>profile</b> :: " + "<b>" + activeProfile + "</b>" +
                    "</span>" +
                "</p>" +
                "<p>" +
                    "<span>" +
                        "<b>server</b> :: " + "<b>" + server + "</b>" +
                    "</span>" +
                "</p>";
    }

    @RequestMapping(value = "/message/{messageCode}", method = RequestMethod.GET, produces = { "text/plain;charset=UTF-8" })
    public String message(@PathVariable("messageCode") String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, null, locale);
    }

    @RequestMapping(value = "/dummies", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<DummyEntity>> getAll() {
        return ResponseEntity.ok().body( dummyService.findAll() );
    }

    @RequestMapping(value = "dummies/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> get(@PathVariable String id) {
        return dummyService
                .findById(id)
                .map( (dummy) -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .eTag(Integer.toString(dummy.getVersion()))
                                .location(new URI("/api/common/dummies/" + dummy.getId()))
                                .body(dummy);
                    } catch (URISyntaxException exc) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/dummies", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<DummyEntity> save(@RequestBody DummyEntity dummy) {
        DummyEntity newDummy = dummyService.save(dummy);
        try {
            return ResponseEntity
                    .created(new URI("/api/common/dummies/" + newDummy.getId()))
                    .eTag(Integer.toString(newDummy.getVersion()))
                    .body(newDummy);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/dummies/{id}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> update(@RequestBody DummyEntity dummy,
                                    @PathVariable String id) {
        return dummyService.update(id, dummy)
                .map( (dummyUpdated) -> {
                    try {
                        return ResponseEntity.ok()
                                .location(new URI("/api/common/dummies/" + dummyUpdated.getId()))
                                .eTag(Integer.toString(dummyUpdated.getVersion()))
                                .body(dummyUpdated);
                    } catch (URISyntaxException exc) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/dummies/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean deleted = dummyService.delete(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

