package com.softserve.booksCatalogPrototype.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.softserve.booksCatalogPrototype.BooksCatalogPrototypeApplication;
import com.softserve.booksCatalogPrototype.controller.util.ObjectConverter;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.softserve.booksCatalogPrototype.dto.LoginRequest;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Publisher;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.repository.BookRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooksCatalogPrototypeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
	AuthorRepository authorRepository;

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

	@Value("${admin.username}")
	private String USERNAME;

	@Value("${admin.password}")
	private String PASSWORD;

	public void fillList(int booksSize, int rate){
		Stream.iterate(0, i -> i)
				.limit(booksSize)
				.map(i -> new Book(null, null,null, null, null, rate, 1, new ArrayList<>(), new LinkedList<>()))
				.forEach(book -> bookRepository.save(book));
	}

	public List<Book> fillList(int size){
		Stream.iterate(0, i -> i)
				.limit(size)
				.map(i -> new BookDTO(null, null,null, null))
				.forEach(bookDTO -> {
					HttpEntity<BookDTO> entity = new HttpEntity<>(bookDTO, header);
					restTemplate.exchange(createURLWithPort("/api/book/add"), HttpMethod.POST, entity, Book.class);
				});
		HttpEntity entity = new HttpEntity(null, header);
		ResponseEntity<List<Book>> response = restTemplate.exchange(
				createURLWithPort("/api/book/all"),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
		return response.getBody();

	}

	public Book fillBook(){
		HttpEntity<BookDTO> entity = new HttpEntity<>(new BookDTO(), header);
		ResponseEntity<Book> response = restTemplate.exchange(createURLWithPort("/api/book/add"), HttpMethod.POST, entity, Book.class);
		return response.getBody();
	}

	public String fillAuthorWithBooks(int booksSize){
		Author author1 = new Author("author1", "author2");
		Author author2 = new Author("author3", "author4");
		String authorId1 = authorRepository.save(author1).getId();
		String authorId2 = authorRepository.save(author2).getId();
		List<String> authorsIds = new ArrayList<>();
		authorsIds.add(authorId1);
		authorsIds.add(authorId2);
		Stream.iterate(0, i -> i)
				.limit(booksSize)
				.map(i -> new BookDTO(null, null,null, authorsIds))
				.forEach(bookDTO -> {
					HttpEntity<BookDTO> entity = new HttpEntity<>(bookDTO, header);
					restTemplate.exchange(createURLWithPort("/api/book/add"), HttpMethod.POST, entity, Book.class);
				});
		return authorId1;
	}

    @Before
    public void setUp() throws Exception{
        ResponseEntity<JwtAuthenticationResponse> response = authenticationController.login(new LoginRequest(USERNAME, PASSWORD));
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
    public void allTest() {
        int booksSize = 5;
	    fillList(booksSize);
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort("/api/book/all"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
        Assert.assertEquals(booksSize, response.getBody().size());
    }

    @Test
    public void allPagesTest() {
        int booksSize = 20;
        int pageNumber = 1;
        int pageSize = 5;
	    fillList(booksSize);
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort("/api/book//all_pagination?pageNumber=" + pageNumber + "&pageSize=" + pageSize),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
        Assert.assertEquals(pageSize, response.getBody().size());
    }

    @Test
    public void getTest() {
        Book book = fillBook();
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<Book> response = restTemplate.exchange(
                createURLWithPort("/api/book/get/" + book.getIsbn()),
                HttpMethod.GET, entity, Book.class);
        Assert.assertEquals(book.getIsbn(), response.getBody().getIsbn());
    }

    @Test
    public void updateTest() throws Exception{
        Book book = fillBook();
        Date yearPublished = new Date();
        String name = "new book";
        Book newBook = new Book(book.getIsbn(), name, yearPublished, null, null, 0.0, 0, null, null);

        HttpEntity entity = new HttpEntity(newBook, header);
        ResponseEntity<Book> response = restTemplate.exchange(
                createURLWithPort("/api/book/update"),
                HttpMethod.PUT, entity, Book.class);

        Assert.assertEquals(yearPublished, response.getBody().getYearPublished());
        Assert.assertEquals(name, response.getBody().getName());
    }

    @Test
    public void deleteTest() {
        Book book = fillBook();
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/book/delete/" + book.getIsbn()),
                HttpMethod.DELETE, entity, String.class);
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void deleteBooksTest() {
        int booksSize = 2;
        List<Book> books = fillList(booksSize);
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/book/bulk_delete?id=" + books.get(0).getIsbn() + "&id=" + books.get(1).getIsbn()),
                HttpMethod.DELETE, entity, String.class);
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void authorTest() {
    	int booksSize = 10;
	    String authorId = fillAuthorWithBooks(booksSize);
	    HttpEntity entity = new HttpEntity(null, header);
	    ResponseEntity<List<Book>> response = restTemplate.exchange(
			    createURLWithPort("/api/book/books/" + authorId),
			    HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
	    Assert.assertEquals(booksSize, response.getBody().size());
    }

    @Test
    public void rateExistsTest() {
        int booksSize = 20;
        int rate = 3;
        int pageNumber = 0;
        int pageSize = 30;
	    fillList(booksSize, rate);

        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort("/api/book/rate_exists?pageNumber=" + pageNumber + "&pageSize=" + pageSize),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
        Assert.assertEquals(booksSize, response.getBody().size());
    }

    @Test
    public void rateTest() {
        int booksSize = 20;
        int rate = 3;
        int pageNumber = 0;
        int pageSize = 5;
	    fillList(booksSize, rate);

        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort("/api/book/rate?rate=" + rate + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Book>>(){});
        Assert.assertEquals(pageSize, response.getBody().size());
    }

    @Test
    public void giveRateTest() {
        Book book = fillBook();
        int rate = 3;
        HttpEntity entity = new HttpEntity(null, header);
        ResponseEntity<Book> response = restTemplate.exchange(
                createURLWithPort("/api/book/give_rate/" + book.getIsbn() + "?rate=" + rate),
                HttpMethod.PUT, entity, Book.class);
        Assert.assertEquals(rate, response.getBody().getRate(), 0.01);
        Assert.assertEquals(1, response.getBody().getTotalVoteCount());
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