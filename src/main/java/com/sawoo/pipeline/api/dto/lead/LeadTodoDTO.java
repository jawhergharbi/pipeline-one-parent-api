package com.sawoo.pipeline.api.dto.lead;

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
public class LeadTodoDTO extends TodoAssigneeDTO {

    private AccountFieldDTO account;

    private LeadTodoLeadDTO lead;
}
