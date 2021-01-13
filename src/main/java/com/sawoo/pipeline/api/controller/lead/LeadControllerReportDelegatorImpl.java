package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.io.ByteArrayInputStream;

@Component
@Qualifier("leadControllerReport")
public class LeadControllerReportDelegatorImpl implements LeadControllerReportDelegator {

    private final LeadService service;

    @Autowired
    public LeadControllerReportDelegatorImpl(LeadService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<InputStreamResource> getReport(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            String template,
            String lan) {
        byte[] pdfBytes = service.getReport(id, template, lan);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment; filename=" + template + "." + id + ".pdf").build());
        return ResponseEntity
                .ok()
                .contentLength(pdfBytes.length)
                .headers(headers)
                .body( new InputStreamResource(new ByteArrayInputStream(pdfBytes)) );
    }
}
