package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI)
public class CampaignController {

    private final CampaignControllerDelegator delegator;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignDTO> create(@RequestBody CampaignDTO dto) {
        return delegator.create(dto);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CampaignDTO>> getAll() {
        return delegator.findAll();
    }

   @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @RequestMapping(
            value = "/{id}/versions",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<CampaignDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody CampaignDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @RequestMapping(
            value = "/accounts/{accountIds}/main",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CampaignDTO>> findByAccountIds(
            @PathVariable(value = "accountIds") Set<String> accountIds) {
        return delegator.findByAccounts(accountIds);
    }

    @RequestMapping(
            value = "/{id}/leads",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignLeadDTO> addCampaignLead(
            @PathVariable("id") String id,
            @RequestBody CampaignLeadAddDTO dto) {
        return delegator.addCampaignLead(id, dto);
    }

    @RequestMapping(
            value = "/{id}/leads/{leadId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignLeadDTO> removeCampaignLead(
            @PathVariable("id") String id,
            @PathVariable("leadId") String leadId) {
        return delegator.removeCampaignLead(id, leadId);
    }
}
