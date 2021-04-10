package com.sawoo.pipeline.api.service.prospect;


import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
@Validated
@Primary
public class ProspectServiceImpl extends BaseServiceImpl<ProspectDTO, Prospect, ProspectRepository, ProspectMapper> implements ProspectService {

    private final ProspectReportService reportService;
    private final ProspectTodoService todoService;
    private final ProspectSequenceTodoService sequenceTodoService;

    @Autowired
    public ProspectServiceImpl(ProspectRepository repository, ProspectMapper mapper,
                               ProspectReportService reportService,
                               ProspectTodoService todoService,
                               ProspectSequenceTodoService sequenceTodoService,
                               ApplicationEventPublisher publisher,
                               AuditService audit) {
        super(repository, mapper, DBConstants.PROSPECT_DOCUMENT, publisher, audit);
        this.reportService = reportService;
        this.todoService = todoService;
        this.sequenceTodoService = sequenceTodoService;
    }

    @Override
    public Optional<Prospect> entityExists(ProspectDTO entityToCreate) {
        String entityId = entityToCreate.getId();
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.PROSPECT_DOCUMENT,
                entityId);
        return entityId == null ? Optional.empty() : getRepository().findById(entityToCreate.getId());
    }

    @Override
    public byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException {
        return reportService.getReport(id, type, lan);
    }

    @Override
    public TodoDTO addTODO(String prospectId, TodoDTO todo) throws ResourceNotFoundException, CommonServiceException {
        return todoService.addTODO(prospectId, todo);
    }

    @Override
    public <T extends TodoDTO> List<TodoDTO> addTODOList(String prospectId, List<T> todoList)
            throws ResourceNotFoundException, CommonServiceException {
        return todoService.addTODOList(prospectId, todoList);
    }

    @Override
    public TodoDTO removeTODO(String prospectId, String todoId) throws ResourceNotFoundException {
        return todoService.removeTODO(prospectId, todoId);
    }

    @Override
    public List<TodoAssigneeDTO> getTODOs(String prospectId) throws ResourceNotFoundException {
        return todoService.getTODOs(prospectId);
    }

    @Override
    public TodoAssigneeDTO getTODO(String prospectId, String todoId) throws ResourceNotFoundException {
        return todoService.getTODO(prospectId, todoId);
    }

    @Override
    public List<ProspectTodoDTO> findBy(List<String> prospectIds, List<Integer> status, List<Integer> types) throws CommonServiceException {
        return todoService.findBy(prospectIds, status, types);
    }

    @Override
    public List<ProspectTodoDTO> searchBy(TodoSearch searchCriteria) {
        return todoService.searchBy(searchCriteria);
    }

    @Override
    public ProspectDTO deleteProspectSummary(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException {
        log.debug("Delete prospect summary for prospect id [{}]", prospectId);
        Consumer<Prospect> setNull = l -> l.setProspectNotes(null);
        return deleteProspectNotes(prospectId, setNull);
    }

    @Override
    public ProspectDTO deleteProspectQualificationComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException {
        log.debug("Delete qualification comments for prospect id [{}]", prospectId);
        Consumer<Prospect> setNull = l -> {
            if (l.getQualification() != null) {
                l.getQualification().setNotes(null);
            }
        };
        return deleteProspectNotes(prospectId, setNull);
    }

    @Override
    public ProspectDTO deleteProspectCompanyComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException {
        log.debug("Delete prospect company comments for prospect id [{}]", prospectId);
        Consumer<Prospect> setNull = l -> l.setCompanyNotes(null);
        return deleteProspectNotes(prospectId, setNull);
    }

    private Prospect findProspectById(String prospectId) throws ResourceNotFoundException {
        return getRepository()
                .findById(prospectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.PROSPECT_DOCUMENT, prospectId }));
    }

    private ProspectDTO deleteProspectNotes(String prospectId, Consumer<Prospect> setNull) {
        Prospect prospect = findProspectById(prospectId);
        setNull.accept(prospect);
        prospect.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        getRepository().save(prospect);
        log.debug("Prospect with id [{}] has been correctly updated", prospectId);
        return getMapper().getMapperOut().getDestination(prospect);
    }

    @Override
    public List<TodoAssigneeDTO> evalTODOs(String prospectId, String sequenceId, String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        return sequenceTodoService.evalTODOs(prospectId, sequenceId, assigneeId);
    }

    @Override
    public List<TodoAssigneeDTO> createTODOs(String prospectId, String sequenceId, String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        return sequenceTodoService.createTODOs(prospectId, sequenceId, assigneeId);
    }
}
