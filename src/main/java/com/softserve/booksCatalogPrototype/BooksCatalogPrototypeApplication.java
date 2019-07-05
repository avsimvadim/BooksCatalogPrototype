package com.softserve.booksCatalogPrototype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BooksCatalogPrototypeApplication {
    private static final Logger logger = LoggerFactory.getLogger(BooksCatalogPrototypeApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BooksCatalogPrototypeApplication.class, args);
        logger.info("Books catalog app is started");
    }

}
