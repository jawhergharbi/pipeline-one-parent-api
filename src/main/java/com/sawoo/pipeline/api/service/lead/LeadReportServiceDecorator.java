package com.sawoo.pipeline.api.service.lead;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.enums.MappingType;
import com.googlecode.jmapper.api.enums.NullPointerControl;
import com.sawoo.pipeline.api.common.contants.CommonConstants;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.PersonalityDTO;
import com.sawoo.pipeline.api.dto.Report;
import com.sawoo.pipeline.api.dto.lead.LeadReportDTO;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.common.CommonDiscAnalysisData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadReportServiceDecorator implements LeadReportService {

    @Value("${app.report-api}")
    private String reportAPI;

    private final JMapper<LeadReportDTO, Lead> mapperOut = new JMapper<>(LeadReportDTO.class, Lead.class);

    private final CommonDiscAnalysisData discAnalysisData;
    private final LeadRepository repository;

    @Override
    public byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException {
        log.debug("Generating lead report. [Lead id: {}, report template type: {}, Language: {}]", id, type, lan);
        if (type == null) {
            type = DomainConstants.LEAD_REPORT_TEMPLATE_REPORT;
        }

        LeadReportDTO leadReport = repository
                .findById(id)
                .map( lead -> mapperOut.getDestination(lead, NullPointerControl.SOURCE, MappingType.ONLY_VALUED_FIELDS))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", id}));

        log.debug("Generating lead report: [Lead id: {}, firstName: {}, lastName: {}]", id, leadReport.getFirstName(), leadReport.getLastName());

        validateReportData(leadReport);

        if (leadReport.getPersonality() != null && leadReport.getPersonality().getType() != null) {
            PersonalityDTO personalityType = discAnalysisData.getDiscType(leadReport.getPersonality().getType());
            if (personalityType != null) {
                leadReport.setPersonality(personalityType);
            }
        }

        return getPDFReport(type, leadReport, lan);
    }

    private void validateReportData(LeadReportDTO leadReport) {
        // TODO implement
        // company comments, lead comment and personality type can not be null
    }

    private byte[] getPDFReport(String template, LeadReportDTO leadReportData, String lan) throws CommonServiceException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = reportAPI + "/api/create-pdf";
        Report<LeadReportDTO> reportBody = Report
                .<LeadReportDTO>builder()
                .template(template)
                .templateData(leadReportData)
                .type(CommonConstants.REPORT_PDF_STREAM)
                .locale(lan)
                .build();
        log.debug("Calling remote report API: [url:{}, report data: {}]", baseUrl, reportBody.toString());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Report<LeadReportDTO>> request = new HttpEntity<>(reportBody, headers);
            URI uri = new URI(baseUrl);
            ResponseEntity<byte[]> result = restTemplate.postForEntity(uri, request, byte[].class);

            if (result.getBody() != null && result.getBody().length > 0) {
                return result.getBody();
            } else {
                throw new CommonServiceException(
                        ExceptionMessageConstants.LEAD_REPORT_GENERATION_STREAM_BUFFER_EMPTY_ERROR,
                        new String[]{baseUrl, reportBody.toString()});
            }
        } catch (URISyntaxException | HttpClientErrorException error) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.LEAD_REPORT_GENERATION_INTERNAL_SERVER_EXCEPTION,
                    new String[]{error.getMessage(), baseUrl, reportBody.toString()});
        }
    }
}
