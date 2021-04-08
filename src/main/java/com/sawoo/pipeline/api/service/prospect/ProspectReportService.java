package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;

public interface ProspectReportService {

    byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException;
}
