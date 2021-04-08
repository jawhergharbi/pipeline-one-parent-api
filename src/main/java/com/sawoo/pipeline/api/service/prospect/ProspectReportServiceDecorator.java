package com.sawoo.pipeline.api.service.prospect;

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
import com.sawoo.pipeline.api.dto.prospect.ProspectReportDTO;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
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
public class ProspectReportServiceDecorator implements ProspectReportService {

    @Value("${app.report-api}")
    private String reportAPI;

    private final JMapper<ProspectReportDTO, Prospect> mapperOut = new JMapper<>(ProspectReportDTO.class, Prospect.class);

    private final CommonDiscAnalysisData discAnalysisData;
    private final ProspectRepository repository;

    @Override
    public byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException {
        log.debug("Generating prospect report. [Prospect id: {}, report template type: {}, Language: {}]", id, type, lan);
        if (type == null) {
            type = DomainConstants.PROSPECT_REPORT_TEMPLATE_REPORT;
        }

        ProspectReportDTO report = repository
                .findById(id)
                .map(p -> mapperOut.getDestination(p, NullPointerControl.SOURCE, MappingType.ONLY_VALUED_FIELDS))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Prospect", id}));

        log.debug("Generating prospect report: [Prospect id: {}, firstName: {}, lastName: {}]", id, report.getFirstName(), report.getLastName());

        validateReportData(report);

        if (report.getPersonality() != null && report.getPersonality().getType() != null) {
            PersonalityDTO personalityType = discAnalysisData.getDiscType(report.getPersonality().getType());
            if (personalityType != null) {
                report.setPersonality(personalityType);
            }
        }

        return getPDFReport(type, report, lan);
    }

    private void validateReportData(ProspectReportDTO report) {
        // TODO implement
        // company comments, prospect comment and personality type can not be null
    }

    private byte[] getPDFReport(String template, ProspectReportDTO reportData, String lan) throws CommonServiceException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = reportAPI + "/api/create-pdf";
        Report<ProspectReportDTO> reportBody = Report
                .<ProspectReportDTO>builder()
                .template(template)
                .templateData(reportData)
                .type(CommonConstants.REPORT_PDF_STREAM)
                .locale(lan)
                .build();
        log.debug("Calling remote report API: [url:{}, report data: {}]", baseUrl, reportBody.toString());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Report<ProspectReportDTO>> request = new HttpEntity<>(reportBody, headers);
            URI uri = new URI(baseUrl);
            ResponseEntity<byte[]> result = restTemplate.postForEntity(uri, request, byte[].class);

            if (result.getBody() != null && result.getBody().length > 0) {
                return result.getBody();
            } else {
                throw new CommonServiceException(
                        ExceptionMessageConstants.PROSPECT_REPORT_GENERATION_STREAM_BUFFER_EMPTY_ERROR,
                        new String[]{baseUrl, reportBody.toString()});
            }
        } catch (URISyntaxException | HttpClientErrorException error) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.PROSPECT_REPORT_GENERATION_INTERNAL_SERVER_EXCEPTION,
                    new String[]{error.getMessage(), baseUrl, reportBody.toString()});
        }
    }
}
