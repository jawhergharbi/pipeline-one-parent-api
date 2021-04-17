package com.sawoo.pipeline.api.dto.prospect;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProspectTodoDTO extends TodoAssigneeDTO {

    private AccountDTO account;

    private ProspectTodoPersonDTO prospect;
}
