package com.booksCatalogPrototype.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.booksCatalogPrototype.BooksCatalogPrototypeApplication;
import com.booksCatalogPrototype.controller.util.ObjectConverter;
import com.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.booksCatalogPrototype.dto.LoginRequest;
import com.booksCatalogPrototype.dto.ReviewDTO;
import com.booksCatalogPrototype.model.Book;
import com.booksCatalogPrototype.model.Publisher;
import com.booksCatalogPrototype.model.Review;
import com.booksCatalogPrototype.repository.BookRepository;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BooksCatalogPrototypeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewControllerTest {

	@Autowired
	private AuthenticationController authenticationController;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
    BookRepository bookRepository;

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

	public Book fillBook(){
		Book book = new Book("Book", new Date(), new Publisher("Cisco"), null);
		Book saved = bookRepository.save(book);
		return saved;
	}

	public Review fillReview(){
		Book book = fillBook();
		ReviewDTO reviewDTO = new ReviewDTO("review", "review");
		HttpEntity<ReviewDTO> entity = new HttpEntity<>(reviewDTO, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/add-review/" + book.getIsbn()),
				HttpMethod.POST, entity, Review.class);
		return response.getBody();
	}

	public Review fillReview(String commenterName, String comment){
		Book book = fillBook();
		ReviewDTO reviewDTO = new ReviewDTO(commenterName, comment);
		HttpEntity<ReviewDTO> entity = new HttpEntity<>(reviewDTO, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/add-review/" + book.getIsbn()),
				HttpMethod.POST, entity, Review.class);
		return response.getBody();
	}

	public Review fillResponse(){
		Review review = fillReview();
		ReviewDTO reviewDTO = new ReviewDTO("some response", "some comment");
		HttpEntity<ReviewDTO> entity = new HttpEntity<>(reviewDTO, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/add-response/" + review.getId()),
				HttpMethod.POST, entity, Review.class);
		return response.getBody();
	}

	public Book fillReviewList(int reviewsSize){
		Book book = fillBook();
		Stream.iterate(0, i -> i)
				.limit(reviewsSize)
				.map(i -> new ReviewDTO("some", "text"))
				.forEach(reviewDTO -> {
					HttpEntity<ReviewDTO> entity = new HttpEntity<>(reviewDTO, header);
					restTemplate.exchange(createURLWithPort("/api/review/add-review/" + book.getIsbn()), HttpMethod.POST, entity, Review.class);
				});
		return book;
	}

	public Review fillResponseList(int responsesSize){
		Review review = fillReview();
		Stream.iterate(0, i -> i)
				.limit(responsesSize)
				.map(i -> new ReviewDTO("some", "text"))
				.forEach(response -> {
					HttpEntity<ReviewDTO> entity = new HttpEntity<>(response, header);
					restTemplate.exchange(createURLWithPort("/api/review/add-response/" + review.getId()), HttpMethod.POST, entity, Review.class);
				});
		return review;
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
	public void addReviewTest() {
		String commenterName = "Vadim";
		String comment = "hellooo everybody";
		ReviewDTO reviewDTO = new ReviewDTO(commenterName, comment);
		Book book = fillBook();

		HttpEntity<ReviewDTO> entity = new HttpEntity<>(reviewDTO, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/add-review/" + book.getIsbn()),
				HttpMethod.POST, entity, Review.class);

		Assert.assertEquals(commenterName, response.getBody().getCommenterName());
		Assert.assertEquals(comment, response.getBody().getComment());
	}

	@Test
	public void addResponseTest() {
		Review review = fillReview();
		String commenterName = "Vlad";
		String comment = "bye";
		ReviewDTO responseDTO = new ReviewDTO(commenterName, comment);

		HttpEntity<ReviewDTO> entity = new HttpEntity<>(responseDTO, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/add-response/" + review.getId()),
				HttpMethod.POST, entity, Review.class);

		Assert.assertEquals(commenterName, response.getBody().getCommenterName());
		Assert.assertEquals(comment, response.getBody().getComment());
	}

	@Test
	public void getTest() throws Exception{
		String commenterName = "Vlad";
		String comment = "blabla";
		Review review = fillReview(commenterName, comment);

		HttpEntity<ReviewDTO> entity = new HttpEntity<>(null, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/get/" + review.getId()),
				HttpMethod.GET, entity, Review.class);

		String expected = ObjectConverter.getJsonReview(commenterName, comment);
		String actual = ObjectConverter.reviewToJson(response);
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	@Test
	public void allReviewsTest() {
		int reviewsSize = 10;
		Book book = fillReviewList(reviewsSize);

		HttpEntity<ReviewDTO> entity = new HttpEntity<>(null, header);
		ResponseEntity<List<Review>> response = restTemplate.exchange(
				createURLWithPort("/api/review/all-reviews/" + book.getIsbn()),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Review>>(){});

		Assert.assertEquals(reviewsSize, response.getBody().size());
	}

	@Test
	public void allResponsesTest() {
		int responsesSize = 9;
		Review review = fillResponseList(responsesSize);

		HttpEntity<ReviewDTO> entity = new HttpEntity<>(null, header);
		ResponseEntity<List<Review>> response = restTemplate.exchange(
				createURLWithPort("/api/review/all-responses/" + review.getId()),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Review>>(){});

		Assert.assertEquals(responsesSize, response.getBody().size());

	}

	@Test
	public void updateTest() {
		Review review = fillReview();
		String commenterName = "New Name";
		String comment = "New comment";
		Review newReview = new Review(review.getId(), commenterName, comment, null, null);

		HttpEntity<Review> entity = new HttpEntity<>(newReview, header);
		ResponseEntity<Review> response = restTemplate.exchange(
				createURLWithPort("/api/review/update"),
				HttpMethod.PUT, entity, Review.class);

		Assert.assertEquals(commenterName, response.getBody().getCommenterName());
		Assert.assertEquals(comment, response.getBody().getComment());
	}

	@Test
	public void deleteReviewTest() {
		Review review = fillReview();
		HttpEntity<Review> entity = new HttpEntity<>(null, header);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/api/review/delete-review/" + review.getId()),
				HttpMethod.DELETE, entity, String.class);
		Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void deleteResponseTest() {
		Review  review = fillResponse();
		HttpEntity<Review> entity = new HttpEntity<>(null, header);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/api/review/delete-response/" + review.getId()),
				HttpMethod.DELETE, entity, String.class);
		Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
	}
}