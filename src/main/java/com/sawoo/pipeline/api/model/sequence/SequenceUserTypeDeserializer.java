package com.sawoo.pipeline.api.model.sequence;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.RepositoryException;

import java.io.IOException;
import java.util.Arrays;

public class SequenceUserTypeDeserializer extends StdDeserializer<SequenceUserType> {

    public SequenceUserTypeDeserializer() {
        this(SequenceUserType.class);
    }
    protected SequenceUserTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SequenceUserType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int type = node.asInt();
        return Arrays
                .stream(SequenceUserType.values())
                .filter(s -> s.getType() == type).findFirst()
                .orElseThrow(() -> new RepositoryException(
                        ExceptionMessageConstants.REPOSITORY_EXCEPTION_DATA_CONVERSATION_ERROR_EXCEPTION,
                        new String[]{
                                SequenceUserType.class.getSimpleName(),
                                String.format("type %d not found within the enumeration {%s}",
                                        type,
                                        Arrays.toString(Arrays.stream(SequenceUserType.values()).toArray()))}));
    }
}
