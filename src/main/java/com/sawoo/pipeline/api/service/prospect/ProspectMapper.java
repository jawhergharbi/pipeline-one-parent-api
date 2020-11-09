package com.sawoo.pipeline.api.service.prospect;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class ProspectMapper implements BaseMapper<ProspectDTO, Prospect> {

    private final JMapper<ProspectDTO, Prospect> mapperOut = new JMapper<>(ProspectDTO.class, Prospect.class);
    private final JMapper<Prospect, ProspectDTO> mapperIn = new JMapper<>(Prospect.class, ProspectDTO.class);
}
