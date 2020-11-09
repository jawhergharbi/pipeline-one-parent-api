package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.CompanyMongoDB;

public class CompanyMockFactory extends BaseMockFactory<CompanyDTO, CompanyMongoDB> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public CompanyMongoDB newEntity(String id) {
        return null;
    }

    @Override
    public CompanyDTO newDTO(String id) {
        return null;
    }
}
