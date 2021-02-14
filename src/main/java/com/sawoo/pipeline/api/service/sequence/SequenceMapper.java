package com.sawoo.pipeline.api.service.sequence;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class SequenceMapper implements BaseMapper<SequenceDTO, Sequence> {

    private final JMapper<SequenceDTO, Sequence> mapperOut = new JMapper<>(SequenceDTO.class, Sequence.class);
    private final JMapper<Sequence, SequenceDTO> mapperIn = new JMapper<>(Sequence.class, SequenceDTO.class);
}
