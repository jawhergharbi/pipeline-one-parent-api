package com.sawoo.pipeline.api.model.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document(collection = DBConstants.CAMPAIGN_DOCUMENT)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Campaign extends BaseEntity  {
    @JMap
    @Id
    private String id;

    @JMap
    private String name;

    @JMap
    private String description;

    @JMap
    private LocalDateTime startDate;

    @JMap
    private LocalDateTime endDate;

    @JMap
    private CampaignStatus status;

    @JMap
    private String componentId;

    @JMap
    private LocalDateTime actualStartDate;

    @JMap
    private LocalDateTime actualEndDate;

    private List<CampaignLead> leads;

    @JMapConversion(from = {"status"}, to = {"status"})
    public CampaignStatus statusConversion(Integer status) {
        return CampaignStatus.fromValue(status);
    }

}
