package com.sawoo.pipeline.api.repository.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenericEventListener extends AbstractMongoEventListener<Object> {

    /*private final MongoTemplate mongoTemplate;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object document = event.getSource();
        ReflectionUtils.doWithFields(document.getClass(), docField -> {
            ReflectionUtils.makeAccessible(docField);

            if (docField.isAnnotationPresent(DBRef.class)) {
                final Object fieldValue = docField.get(document);

                // Save child
                this.mongoTemplate.save(fieldValue);
            }
        });
    }*/
}
