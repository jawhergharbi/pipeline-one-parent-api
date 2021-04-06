package com.sawoo.pipeline.api.dto.prospect;

import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
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

    private AccountFieldDTO account;

    private ProspectTodoPersonDTO prospect;
}
