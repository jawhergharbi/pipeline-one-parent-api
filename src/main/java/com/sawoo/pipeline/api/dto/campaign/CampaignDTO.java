package com.sawoo.pipeline.api.dto.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class CampaignDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
    private String name;

    @JMap
    private String description;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private LocalDateTime startDate;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private LocalDateTime endDate;

    @JMap
    private Integer status;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
    private String componentId;

    @JMap
    private LocalDateTime actualStartDate;

    @JMap
    private LocalDateTime actualEndDate;

    private List<CampaignLeadDTO> leads;

    private AccountFieldDTO account;

    public List<CampaignLeadDTO> getLeads() {
        if (leads == null) {
            leads = new ArrayList<>();
        }
        return leads;
    }

    @JMapConversion(from = {"status"}, to = {"status"})
    public Integer statusConversion(CampaignStatus status) {
        return status != null ? status.getValue() : -1;
    }
}
