package com.sawoo.pipeline.api.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.DiscTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommonDiscAnalysisData {

    private final ResourceLoader resourceLoader;

    private List<DiscTypeDTO> discTypes;

    public DiscTypeDTO getDiscType(int id) {
        if (discTypes == null) {
            initDiscTypeList();
        }
        return discTypes
                .stream()
                .filter(dt -> dt.getType().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void initDiscTypeList() {
        String discAnalysisResourcePath = "./static/personality/disc-analysis.json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Resource resource = resourceLoader.getResource("classpath:" + discAnalysisResourcePath);
            DiscTypeDTO[] discAnalysisTypes =
                    objectMapper
                            .readValue(resource.getInputStream(), DiscTypeDTO[].class);
            discTypes = Arrays.asList(discAnalysisTypes);
        } catch (IOException exc) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.COMMON_DISC_ANALYSIS_LOADING_EXCEPTION,
                    new Object[] { discAnalysisResourcePath, exc} );
        }
    }
}


