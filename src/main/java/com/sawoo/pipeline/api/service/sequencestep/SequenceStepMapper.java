package com.sawoo.pipeline.api.service.sequencestep;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class SequenceStepMapper implements BaseMapper<SequenceStepDTO, SequenceStep> {

    private final JMapper<SequenceStepDTO, SequenceStep> mapperOut = new JMapper<>(SequenceStepDTO.class, SequenceStep.class);
    private final JMapper<SequenceStep, SequenceStepDTO> mapperIn = new JMapper<>(SequenceStep.class, SequenceStepDTO.class);
}
