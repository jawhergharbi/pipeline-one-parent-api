package com.sawoo.pipeline.api.model.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.person.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Document(collection = DBConstants.LEAD_DOCUMENT)
public class Lead extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    @DBRef
    private Person person;

    @JMap
    private String linkedInThread;

    @JMap
    private Note leadNotes;

    @JMap
    private Note companyNotes;

    @JMap
    private Status status;

    @DBRef
    private List<Todo> todos;
    public List<Todo> getTodos() {
        if (todos == null) {
            todos = new ArrayList<>();
        }
        return todos;
    }

    @JMapConversion(from = {"companyNotes"}, to = {"reportCompanyNotes"})
    public String companyNotesConversion(Note companyNotes) {
        return companyNotes == null ? "" : companyNotes.getText();
    }

    @JMapConversion(from = {"leadNotes"}, to = {"notes"})
    public String leadNotesConversion(Note leadNotes) {
        return leadNotes == null ? "" : leadNotes.getText();
    }
}
