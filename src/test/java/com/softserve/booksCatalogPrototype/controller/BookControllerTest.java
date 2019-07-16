package com.softserve.booksCatalogPrototype.controller;

import com.softserve.booksCatalogPrototype.BooksCatalogPrototypeApplication;
import com.softserve.booksCatalogPrototype.controller.util.ObjectConverter;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.softserve.booksCatalogPrototype.dto.LoginRequest;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Publisher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooksCatalogPrototypeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    MongoTemplate mongoTemplate;

    @LocalServerPort
    private int port;

    protected TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders header;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    public void fill(int size){
        Stream.iterate(0, i -> i)
                .limit(size)
                .map(i -> new BookDTO(i.toString(), null,null, null))
                .forEach(bookDTO -> {
                    HttpEntity<BookDTO> entity = new HttpEntity<>(bookDTO, header);
                    restTemplate.exchange(createURLWithPort("/api/book/add"), HttpMethod.POST, entity, Book.class);
                });
    }

    @Before
    public void setUp() throws Exception{
        ResponseEntity<JwtAuthenticationResponse> response = authenticationController.login(new LoginRequest("BooksAdmin", "00000000"));
        header = new HttpHeaders();
        header.set("Authorization", "Bearer " + response.getBody().getAccessToken());
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.getDb().getCollection("book").drop();
        mongoTemplate.getDb().getCollection("author").drop();
        mongoTemplate.getDb().getCollection("review").drop();
    }

    @Test
    public void addTest() throws Exception{
        String name = "Michell";
        Date yearPublished = new Date();
        Publisher publisher = new Publisher("Yahoo");
        BookDTO bookDTO = new BookDTO(name, yearPublished, publisher, null);

        HttpEntity<BookDTO> entity = new HttpEntity<>(bookDTO, header);
        ResponseEntity<Book> response = restTemplate.exchange(
                createURLWithPort("/api/book/add"),
                HttpMethod.POST, entity, Book.class);

        String actual = ObjectConverter.bookToJson(response);
        String expected = ObjectConverter.getJsonBook(name, yearPublished, publisher, null);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
        Assert.assertNotNull(response.getBody().getCreationDate());
        Assert.assertNotNull(response.getBody().getIsbn());
        Assert.assertEquals(0.0,response.getBody().getRate(), 0.01);
        Assert.assertEquals(0, response.getBody().getTotalVoteCount());
    }

    @Test
    public void all() {
        int booksSize = 5;
        fill(booksSize);
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort("/api/book/all"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
        Assert.assertEquals(booksSize, response.getBody().size());
    }

    @Test
    public void allPages() {
        int booksSize = 20;
        int pageNumber = 1;
        int pageSize = 5;
        fill(booksSize);
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort("/api/book//all_pagination?pageNumber=" + pageNumber + "&pageSize=" + pageSize),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
        Assert.assertEquals(pageSize, response.getBody().size());
    }

    @Test
    public void get() {
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void deleteBooks() {
    }

    @Test
    public void author() {
    }

    @Test
    public void rateExists() {
    }

    @Test
    public void rate() {
    }

    @Test
    public void giveRate() {
    }

    @Test
    public void uploadCover() {
    }

    @Test
    public void getCover() {
    }

    @Test
    public void deleteCover() {
    }

    @Test
    public void uploadContent() {
    }

    @Test
    public void getContent() {
    }

    @Test
    public void deleteContent() {
    }
}