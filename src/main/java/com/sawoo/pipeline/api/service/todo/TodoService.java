package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoSearchDTO;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface TodoService extends BaseService<TodoDTO>, BaseProxyService<TodoRepository, TodoMapper> {

    List<TodoDTO> searchBy(List<String> componentIds, List<Integer> status, List<Integer> channels);

    List<TodoDTO> searchBy(@NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearch search);

    long remove(@Valid @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearchDTO search);

    List<TodoDTO> findAllAndRemove(@Valid @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearchDTO search);

}
