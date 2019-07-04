package com.softserve.booksCatalogPrototype.config;

import com.softserve.booksCatalogPrototype.event.CascadeMongoEventListener;
import com.softserve.booksCatalogPrototype.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableMongoAuditing
public class AppConfig{

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    public MongoDbFactory getMongoDbFactory() {
        return mongoDbFactory;
    }

    public MappingMongoConverter getMappingMongoConverter() {
        return mappingMongoConverter;
    }

    @Bean
    public CascadeMongoEventListener cascadingMongoEventListener() {
        return new CascadeMongoEventListener();
    }

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return new GridFsTemplate(getMongoDbFactory(), getMappingMongoConverter());
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.softserve.booksCatalogPrototype")).build();
    }

}
