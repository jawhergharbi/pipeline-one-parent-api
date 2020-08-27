package com.sawoo.pipeline.api.model.client;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.Status;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.lead.Lead;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = DataStoreConstants.CLIENT_ENTITY_ENTITY)
public class Client {

    @JMap
    @Id
    private Long id;

    @JMap
    private String fullName;

    @JMap
    private String position;

    @JMap
    private String email;

    @JMap
    @Field(name = "phone_number")
    private String phoneNumber;

    @JMap
    @Field(name = "linked_in_url")
    private String linkedInUrl;

    @JMap
    private Note notes;

    @JMap
    @Reference
    private Company company;

    @JMap
    private Status status;

    @Reference
    private List<Lead> leads;

    @JMap
    @Reference
    private User salesAssistant;

    @JMap
    @Reference
    private User customerSuccessManager;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;

    @JMapConversion(from = {"leads"}, to = {"leadsSize"})
    public int conversion(List<Lead> leads) {
        return leads == null ? 0 : leads.size();
    }

    public List<Lead> getLeads() {
        if (leads == null) {
            leads = new ArrayList<>();
        }
        return leads;
    }
}
