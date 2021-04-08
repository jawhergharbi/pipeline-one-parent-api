package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.model.account.AccountStatus;
import com.sawoo.pipeline.api.model.common.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI)
public class AccountController {

    private final AccountControllerDelegator delegator;

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> create(@RequestBody AccountDTO dto) {
        if (dto.getStatus() == null) {
            dto.setStatus(Status.builder().value(AccountStatus.ON_BOARDING.getValue()).build());
        }
        return delegator.create(dto);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AccountDTO>> getAll() {
        return delegator.findAll();
    }

   @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<AccountDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody AccountDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @DeleteMapping(
            value = "/{id}/notes",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> deleteAccountNotes(@PathVariable String id) {
        return delegator.deleteAccountNotes(id);
    }

    @DeleteMapping(
            value = "/{id}/company-notes",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountDTO> deleteProspectCompanyComments(@PathVariable String id) {
        return delegator.deleteAccountCompanyNotes(id);
    }

    @GetMapping(
            value = "/user/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AccountDTO>> findByUserId(
            @PathVariable("id") String userId) {
        return delegator.findByUserId(userId);
    }

    @PutMapping(
            value = "/{id}/user/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateUser(
            @PathVariable("id") String id,
            @PathVariable("userId") String userId) {
        return delegator.updateUser(id, userId);
    }

    @DeleteMapping(
            value = "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> removeProspect(
            @PathVariable("id") String accountId,
            @PathVariable(ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME) String prospectId) {
        return delegator.removeProspect(accountId, prospectId);
    }

    @GetMapping(
            value = "/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProspectDTO>> findAllProspects(
            @PathVariable("id") String accountId) {
        return delegator.findAllProspects(accountId);
    }

    @GetMapping(
            value = "/{ids}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/main",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProspectDTO>> findAllProspects(
            @NotNull
            @PathVariable("ids") String[] ids,
            @RequestParam(value = "status", required = false) Integer[] prospectStatus) {
        return delegator.findAllProspects(ids, prospectStatus);
    }

    @PostMapping(
            value = {"/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME,
                    "/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/{type}"},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> create(
            @PathVariable(value = "id") String accountId,
            @NotNull @RequestBody ProspectDTO prospect) {
        return delegator.createProspect(accountId, prospect);
    }

    @GetMapping(
            value = "/{ids}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/main",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProspectTodoDTO>> findAllTODOs(
            @NotNull
            @PathVariable("ids") List<String> ids,
            @RequestParam(value = "status", required = false) List<Integer> status,
            @RequestParam(value = "types", required = false) List<Integer> types) {
        return delegator.findAllTODOs(ids, status, types);
    }
}
