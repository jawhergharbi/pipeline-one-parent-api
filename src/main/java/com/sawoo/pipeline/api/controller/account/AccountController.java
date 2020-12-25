package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTypeRequestParam;
import com.sawoo.pipeline.api.model.account.AccountStatus;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI)
public class AccountController {

    private final AccountControllerDelegator delegator;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> create(@RequestBody AccountDTO dto) {
        if (dto.getStatus() == null) {
            dto.setStatus(Status.builder().value(AccountStatus.ON_BOARDING.getValue()).build());
        }
        return delegator.create(dto);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AccountDTO>> getAll() {
        return delegator.findAll();
    }

   @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody AccountDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @RequestMapping(
            value = "/user/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AccountDTO>> findByUserId(
            @PathVariable("id") String userId) {
        return delegator.findByUserId(userId);
    }

    @RequestMapping(
            value = "/{id}/user/{userId}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateUser(
            @PathVariable("id") String id,
            @PathVariable("userId") String userId) {
        return delegator.updateUser(id, userId);
    }

    @RequestMapping(
            value = "/{id}/leads/{leadId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> removeLead(
            @PathVariable("id") String accountId,
            @PathVariable("leadId") String leadId) {
        return delegator.removeLead(accountId, leadId);
    }

    @RequestMapping(
            value = "/{id}/leads",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadDTO>> findLeadsAll(
            @PathVariable("id") String accountId) {
        return delegator.findAllLeads(accountId);
    }

    @RequestMapping(
            value = {"/{id}/leads", "/{id}/leads/{type}"},
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> create(
            @PathVariable(value = "id") String accountId,
            @PathVariable(value = "type", required = false) LeadTypeRequestParam type,
            @NotNull @RequestBody LeadDTO lead) {
        if (type != null && type.equals(LeadTypeRequestParam.LEAD)) {
            lead.setStatus(Status
                    .builder()
                    .value(LeadStatusList.HOT.getStatus())
                    .updated(LocalDateTime.now()).build());
        } else {
            lead.setStatus(Status
                    .builder()
                    .value(LeadStatusList.FUNNEL_ON_GOING.getStatus())
                    .updated(LocalDateTime.now())
                    .build());
        }
        return delegator.createLead(accountId, lead);
    }
}
