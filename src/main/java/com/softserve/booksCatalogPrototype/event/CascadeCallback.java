package com.softserve.booksCatalogPrototype.event;

import com.softserve.booksCatalogPrototype.annotations.CascadeSave;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class CascadeCallback implements ReflectionUtils.FieldCallback {

    private Object source;
    private MongoOperations mongoOperations;

    CascadeCallback(Object source, MongoOperations mongoOperations) {
        this.source = source;
        this.setMongoOperations(mongoOperations);
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public MongoOperations getMongoOperations() {
        return mongoOperations;
    }

    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.makeAccessible(field);
        if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
            Object fieldValue = field.get(getSource());
            if (fieldValue != null) {
                if (List.class.equals(field.getType())) {
                    for (Object obj : (List)fieldValue) {
                        getMongoOperations().save(obj);
                    }
                } else {
                    if (fieldValue != null) {
                        FieldCallback callback = new FieldCallback();
                        ReflectionUtils.doWithFields(fieldValue.getClass(), callback);
                        getMongoOperations().save(fieldValue);
                    }
                }
            }
        }
    }

}
