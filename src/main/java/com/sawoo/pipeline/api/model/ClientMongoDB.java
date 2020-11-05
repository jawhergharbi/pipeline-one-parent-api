package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.common.NoteMongoDB;
import com.sawoo.pipeline.api.model.common.StatusMongoDB;
import com.sawoo.pipeline.api.model.lead.Lead;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = DataStoreConstants.CLIENT_DOCUMENT)
public class ClientMongoDB {

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
    private NoteMongoDB notes;

    @JMap
    @DBRef
    private CompanyMongoDB company;

    @JMap
    private StatusMongoDB status;

    @Reference
    private List<Lead> leads;

    @JMap
    @DBRef
    private UserMongoDB salesAssistant;

    @JMap
    @DBRef
    private UserMongoDB customerSuccessManager;

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
