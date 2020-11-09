package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.googlecode.jmapper.api.enums.NullPointerControl;
import com.sawoo.pipeline.api.common.contants.CommonConstants;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.DiscTypeDTO;
import com.sawoo.pipeline.api.dto.ReportDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadReportDataDTO;
import com.sawoo.pipeline.api.model.prospect.Lead;
import com.sawoo.pipeline.api.repository.LeadRepository;
import com.sawoo.pipeline.api.service.common.CommonDiscAnalysisData;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadServiceImpl implements LeadService {

    @Value("${app.report-api}")
    private String reportAPI;

    private final CommonDiscAnalysisData discAnalysisData;
    private final CommonServiceMapper mapper;
    private final LeadServiceUtils utils;
    private final LeadRepository repository;

    @Override
    public LeadDTO create(LeadDTO lead, int type) throws CommonServiceException {
        log.debug("Creating new prospect. Name: [{}]. Type: [{}]", lead.getFullName(), type);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        utils.preProcessLead(lead, now, type);

        Lead entity = mapper.getLeadDTOToDomainMapper().getDestination(lead);
        entity = repository.save(entity);

        log.debug("Prospect has been successfully created. Entity: [{}]", entity);

        return mapper.getLeadDomainToDTOMapper().getDestination(entity);
    }

    @Override
    public LeadDTO findById(Long id) throws ResourceNotFoundException {
        log.debug("Retrieving lead by id. Id: [{}]", id);

        return repository
                .findById(id)
                .map(mapper.getLeadDomainToDTOMapper()::getDestination)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", String.valueOf(id)}));
    }

    @Override
    public List<LeadDTO> findAll() {
        log.debug("Retrieving all lead entities");
        List<LeadDTO> leads = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(mapper.getLeadDomainToDTOMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] lead/s has/have been found", leads.size());
        return leads;
    }

    @Override
    public List<LeadMainDTO> findAllMain(LocalDateTime datetime) {
        log.debug("Retrieve all lead entities together with their next and previous interactions. Date time: [{}]", datetime);

        List<LeadMainDTO> leads = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map((lead) -> mapper.getLeadDomainToDTOMainMapper().getDestination(lead))
                .collect(Collectors.toList());
        log.debug("[{}] leads has been found", leads.size());
        return leads;
    }

    @Override
    public Optional<LeadDTO> delete(Long id) {
        log.debug("Deleting lead entity with id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> {
                    repository.delete(company);
                    log.debug("Lead entity with id: [{}] has been deleted", id);
                    return Optional.of(mapper.getLeadDomainToDTOMapper().getDestination(company));
                })
                .orElseGet(() -> {
                    log.info("Lead entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public Optional<LeadDTO> update(Long id, LeadDTO leadDTO) {
        log.debug("Updating lead with id: [{}]. Lead: [{}]", id, leadDTO);

        return repository
                .findById(id)
                .map((lead) -> {
                    lead = mapper
                            .getLeadDTOToDomainMapper()
                            .getDestination(
                                    lead,
                                    leadDTO,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(lead);

                    log.debug("Lead entity with id [{}] has been successfully updated. Updated data: [{}]", id, leadDTO);
                    return Optional.of(mapper.getLeadDomainToDTOMapper().getDestination(lead));
                })
                .orElseGet(() -> {
                    log.info("Lead entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public byte[] getReport(Long id, String template, String lan) throws CommonServiceException, ResourceNotFoundException {
        log.debug("Generating lead report. Lead id: [{}]. Report template: [{}]. Language: [{}]", id, template, lan);
        if (template == null) {
            template = DomainConstants.PROSPECT_REPORT_TEMPLATE_REPORT;
        }

        LeadReportDataDTO leadReportData = repository
                .findById(id)
                .map( (lead) -> mapper.getLeadDomainToReportDTOMapper().getDestination(lead, NullPointerControl.SOURCE, MappingType.ONLY_VALUED_FIELDS))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", String.valueOf(id)}));

        log.debug("Generating lead report. Lead id: [{}], Name: [{}]", id, leadReportData.getFullName());

        // TODO externalize to a different component
        validateReportData(leadReportData);

        if (leadReportData.getPersonality().getType() != null) {
            DiscTypeDTO discType = discAnalysisData.getDiscType(leadReportData.getPersonality().getType());
            if (discType != null) {
                leadReportData.setPersonality(discType);
            }
        }

        return getPDFReport(template, leadReportData, lan);
    }

    private void validateReportData(LeadReportDataDTO reportData) {
        // TODO implement
        // company comments, lead comment and personality type can not be null
    }

    // TODO externalize to a different component
    private byte[] getPDFReport(String template, LeadReportDataDTO leadReportData, String lan) throws CommonServiceException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = reportAPI + "/api/create-pdf";
        ReportDTO<LeadReportDataDTO> reportBody = ReportDTO
                .<LeadReportDataDTO>builder()
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

            HttpEntity<ReportDTO<LeadReportDataDTO>> request = new HttpEntity<>(reportBody, headers);
            URI uri = new URI(baseUrl);
            ResponseEntity<byte[]> result = restTemplate.postForEntity(uri, request, byte[].class);

            if (result.getBody() == null || result.getBody().length == 0) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.LEAD_REPORT_GENERATION_STREAM_BUFFER_EMPTY_ERROR,
                        new String[]{baseUrl, reportBody.toString()});
            }
            return result.getBody();
        } catch (URISyntaxException | HttpClientErrorException error) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.LEAD_REPORT_GENERATION_INTERNAL_SERVER_EXCEPTION,
                    new String[]{error.getMessage(), baseUrl, reportBody.toString()});
        }
    }
}
