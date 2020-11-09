package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import org.springframework.stereotype.Component;

@Component
public class UserAuthServiceEventListener implements BaseServiceEventListener<UserAuthDTO, UserMongoDB> {
    @Override
    public void onBeforeCreate(UserAuthDTO dto, UserMongoDB entity) {
        // nothing
    }

    @Override
    public void onBeforeUpdate(UserAuthDTO dto, UserMongoDB entity) {
        // nothing
    }
}
