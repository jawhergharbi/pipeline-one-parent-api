package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;

public interface LeadReportService {

    byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException;
}
