package com.softserve.booksCatalogPrototype.event;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

public class CascadeMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), new CascadeSaveCallback(source, mongoOperations));
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
        Document document = event.getDocument();
        if(document != null) {
            Query query = new BasicQuery(document);
            Object objectToDelete = mongoOperations.findOne(query, event.getType());
            if(objectToDelete != null) {
                ReflectionUtils.doWithFields(objectToDelete.getClass(), new CascadeDeleteCallback(objectToDelete, mongoOperations));
            }
        }
    }

}
