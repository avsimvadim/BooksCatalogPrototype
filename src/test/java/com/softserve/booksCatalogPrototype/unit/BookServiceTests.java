package com.softserve.booksCatalogPrototype.unit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.repository.BookRepository;
import com.softserve.booksCatalogPrototype.service.BookServiceImpl;
import com.softserve.booksCatalogPrototype.unit.util.GetObjects;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTests {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private GridFsOperations gridFsOperations;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    BookServiceImpl bookService;

    @After
    public void tearDown() throws Exception {
        bookRepository = null;
        authorRepository = null;
        mongoOperations = null;
        gridFsOperations = null;
        gridFsTemplate = null;
    }

    @Test
    public void saveTest() {
        Book book = GetObjects.getBook();
        bookService.save(book);
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    public void getAllTest() {
        bookService.getAll();
        verify(bookRepository, times(1)).findAll();
        int listSize = 5;
        when(bookRepository.findAll()).thenReturn(GetObjects.getBooksList(listSize));
        Assert.assertEquals(listSize,bookService.getAll().size());
    }

    @Test
    public void getTest() {
        Book book = GetObjects.getFilledBook();
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        Assert.assertEquals("1", bookService.get("1").getIsbn());
    }

    @Test
    public void deleteTest() {
        Book book = GetObjects.getFilledBook();
        doNothing().when(gridFsTemplate);
        bookService.delete(book);
        verify(bookRepository, times(1)).delete(any());
    }

    @Test
    public void updateTest() {
        Book book = GetObjects.getBook("name", new Date(), "name");
        when(bookRepository.findById(null)).thenReturn(Optional.of(book));
        bookService.update(GetObjects.getBook());
        verify(bookRepository, times(2)).findById(any());
        Assert.assertEquals(book, bookService.update(book));
    }

    @Test
    public void getAllPageableTest() {
        PageRequest pageRequest = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "rate"));
        bookService.getAll(pageRequest);
        verify(bookRepository, times(1)).findAll(pageRequest);
        int listSize = 10;
        Page<Book> pagedResponse = new PageImpl<>(GetObjects.getBooksList(listSize));
        when(bookRepository.findAll(pageRequest)).thenReturn(pagedResponse);
        Assert.assertEquals(listSize, bookService.getAll(pageRequest).getTotalElements());
    }

    @Test
    public void getBooksByAuthorTest() {
        String authorid = "fsfsvf44";
        int bookListSize = 3;
        when(authorRepository.findById(authorid)).thenReturn(Optional.of(GetObjects.getAuthorWithId(authorid)));
        when(bookRepository.findBooksByAuthors(GetObjects.getAuthorWithId(authorid))).thenReturn(GetObjects.getBooksList(bookListSize));
        List<Book> actual = bookService.getBooksByAuthor(authorid);
        System.out.println(actual.toString());
        Assert.assertEquals(bookListSize, actual.size());
    }

    @Test
    public void withRatePageableTest() {
        int bookListSize = 3;
        PageRequest pageRequest = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "rate"));
        when(bookRepository.findAllByRateIsNot(0, pageRequest)).thenReturn(GetObjects.getBooksList(bookListSize));
        Assert.assertEquals(bookListSize, bookService.withRate(pageRequest).size());
    }

    @Test
    public void withRateAndPageableTest() {
        int page = 0;
        int pageSize = 5;
        PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "rate"));
        when(bookRepository.findAllByRateIsNot(0, pageRequest)).thenReturn(GetObjects.getBooksList(pageSize));
        Assert.assertEquals(pageSize, bookService.withRate(pageRequest).size());
    }

    @Test
    public void giveRateTest() {
        Book book = GetObjects.getFilledBook();
        book.setRate(5);
        book.setTotalVoteCount(1);
        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        Assert.assertEquals(3.0, bookService.giveRate(book.getIsbn(), 1).getRate(), 0.01);
    }

    @Test
    public void deleteBooksTest() {
        String[] ids = {"1","2","2","4"};
        when(bookRepository.findById("1")).thenReturn(Optional.of(GetObjects.getBook("1")));
        when(bookRepository.findById("2")).thenReturn(Optional.of(GetObjects.getBook("2")));
        when(bookRepository.findById("4")).thenReturn(Optional.of(GetObjects.getBook("4")));
        bookService.deleteBooks(ids);
        verify(bookRepository, times(6)).findById(any());
        verify(bookRepository, times(3)).delete(any());
    }

//    @Test
//    public void uploadBookCover() throws Exception{
//        DBObject metaData = new BasicDBObject();
//        metaData.put("bookId", "id");
//        MultipartFile multipartFile = GetObjects.getMultipartFileImage();
//        when(gridFsOperations.store(any(InputStream.class), anyString(), anyString(), any(DBObject.class))).thenReturn(new ObjectId());
//        bookService.uploadBookCover(multipartFile,"id");
//        verify(gridFsOperations, times(1)).store(any(InputStream.class), anyString(), anyString(), any(DBObject.class));
//    }

    @Test
    public void getBookCover() {
    }

    @Test
    public void deleteBookCover() {
        bookService.deleteBookCover("id");
        verify(gridFsOperations, times(1)).delete(any());
    }

//    @Test
//    public void uploadBookContent() {
//        DBObject metaData = new BasicDBObject();
//        metaData.put("bookId", "id");
//        MultipartFile multipartFile = GetObjects.getMultipartFileImage();
//        when(gridFsOperations.store(any(InputStream.class), anyString(), anyString(), any(DBObject.class))).thenReturn(new ObjectId());
//        bookService.uploadBookContent(multipartFile,"id");
//        verify(gridFsOperations, times(1)).store(any(InputStream.class), anyString(), anyString(), any(DBObject.class));
//    }

    @Test
    public void getBookContent() {
    }

    @Test
    public void deleteBookContent() {
        bookService.deleteBookContent("id");
        verify(gridFsOperations, times(1)).delete(any());
    }

    @Test
    public void findBookWithReviewTest() {
        Review review = new Review("id", "vadim", "comment", null, null);
        when(bookRepository.findBookByReviewsIs(review)).thenReturn(GetObjects.getBook(review));
        Assert.assertEquals(1,bookService.findBookWithReview(review).getReviews().size());
    }
}
