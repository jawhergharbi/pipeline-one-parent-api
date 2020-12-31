package com.sawoo.pipeline.api.model.client;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.CompanyOld;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.UserOld;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.LeadOld;
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
@Entity(name = DBConstants.CLIENT_DOCUMENT)
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
    private CompanyOld company;

    @JMap
    private Status status;

    @Reference
    private List<LeadOld> leads;

    @JMap
    @Reference
    private UserOld salesAssistant;

    @JMap
    @Reference
    private UserOld customerSuccessManager;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;

    @JMapConversion(from = {"leads"}, to = {"leadsSize"})
    public int conversion(List<LeadOld> leads) {
        return leads == null ? 0 : leads.size();
    }

    public List<LeadOld> getLeads() {
        if (leads == null) {
            leads = new ArrayList<>();
        }
        return leads;
    }
}
