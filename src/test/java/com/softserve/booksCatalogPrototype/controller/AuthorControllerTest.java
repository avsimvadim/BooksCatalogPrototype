package com.softserve.booksCatalogPrototype.controller;

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
import com.softserve.booksCatalogPrototype.dto.AuthorDTO;
import com.softserve.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.softserve.booksCatalogPrototype.dto.LoginRequest;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooksCatalogPrototypeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

	@Autowired
	private AuthenticationController authenticationController;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	AuthorRepository authorRepository;

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

	private List<Author> fillList(int authorsSize){
		Stream.iterate(0, i -> i)
				.limit(authorsSize)
				.map(i -> new Author())
				.forEach(author -> authorRepository.save(author));
		HttpEntity entity = new HttpEntity(null, header);
		ResponseEntity<List<Author>> response = restTemplate.exchange(
				createURLWithPort("/api/author/all"),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Author>>(){});
		return response.getBody();
	}

	private Author fillAuthor(){
		HttpEntity<AuthorDTO> entity = new HttpEntity<>(new AuthorDTO(), header);
		ResponseEntity<Author> response = restTemplate.exchange(createURLWithPort("/api/author/add"), HttpMethod.POST, entity, Author.class);
		return response.getBody();
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
		String firstName = "Vadim";
		String secondName = "Tahiiev";
		AuthorDTO authorDTO = new AuthorDTO(firstName, secondName);

		HttpEntity<AuthorDTO> entity = new HttpEntity<>(authorDTO, header);
		ResponseEntity<Author> response = restTemplate.exchange(
				createURLWithPort("/api/author/add"),
				HttpMethod.POST, entity, Author.class);

		String actual = ObjectConverter.authorToJson(response);
		String expected = ObjectConverter.getJsonAuthor(firstName, secondName);
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
		Assert.assertEquals(firstName, response.getBody().getFirstName());
		Assert.assertEquals(secondName, response.getBody().getSecondName());
	}

	@Test
	public void allTest() {
		int authorSize = 5;
		fillList(authorSize);
		HttpEntity entity = new HttpEntity(null, header);
		ResponseEntity<List<Author>> response = restTemplate.exchange(
				createURLWithPort("/api/author/all"),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Author>>(){});
		Assert.assertEquals(authorSize, response.getBody().size());
	}

	@Test
	public void allPagesTest() {
		int pageNumber = 0;
		int pageSize = 5;
		int authorSize = 10;
		fillList(authorSize);
		HttpEntity entity = new HttpEntity(null, header);
		ResponseEntity<List<Author>> response = restTemplate.exchange(
				createURLWithPort("/api/author//all-pagination?pageNumber=" + pageNumber + "&pageSize=" + pageSize),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Author>>(){});
		Assert.assertEquals(pageSize, response.getBody().size());
	}

	@Test
	public void getTest() {
		Author author = fillAuthor();
		HttpEntity entity = new HttpEntity(null, header);
		ResponseEntity<Author> response = restTemplate.exchange(
				createURLWithPort("/api/author/get/" + author.getId()),
				HttpMethod.GET, entity, Author.class);
		Assert.assertEquals(author.getId(), response.getBody().getId());
	}

	@Test
	public void updateTest() {
		Author author = fillAuthor();
		String firstName = "Vadim";
		String secondName = "Tahiiev";
		Author newAuthor = new Author(author.getId(), firstName, secondName, null);

		HttpEntity<Author> entity = new HttpEntity<>(newAuthor, header);
		ResponseEntity<Author> response = restTemplate.exchange(
				createURLWithPort("/api/author/update"),
				HttpMethod.PUT, entity, Author.class);

		Assert.assertEquals(firstName, response.getBody().getFirstName());
		Assert.assertEquals(secondName, response.getBody().getSecondName());
	}

	@Test
	public void deleteTest() {
		Author author = fillAuthor();
		HttpEntity<Author> entity = new HttpEntity<>(null, header);
		ResponseEntity<Author> response = restTemplate.exchange(
				createURLWithPort("/api/author/delete/" + author.getId()),
				HttpMethod.DELETE, entity, Author.class);

		Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void deleteAuthorsTest() {
		int authorsSize = 2;
		List<Author> authors = fillList(authorsSize);
		HttpEntity entity = new HttpEntity(null, header);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/api/author/bulk-delete?id=" + authors.get(0).getId() + "&id=" + authors.get(1).getId()),
				HttpMethod.DELETE, entity, String.class);
		Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
	}
}