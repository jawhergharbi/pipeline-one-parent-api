package com.sawoo.pipeline.api.model.account;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = DBConstants.ACCOUNT_DOCUMENT)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Account extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    @TextIndexed
    private String fullName;

    @JMap
    private String position;

    @JMap
    private String email;

    @JMap
    private String phoneNumber;

    @JMap
    @Indexed
    private String linkedInUrl;

    @JMap
    private Note notes;

    @JMap
    @DBRef
    private Company company;

    @JMap
    private Status status;

    @JMap
    @DBRef
    private Set<User> users;

    @DBRef(lazy = true)
    private List<Lead> leads;

    public Set<User> getUsers() {
        if (users == null) {
            users = new HashSet<>();
        }
        return users;
    }

    public List<Lead> getLeads() {
        if (leads == null) {
            leads = new ArrayList<>();
        }
        return leads;
    }
}
