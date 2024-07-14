package com.booksCatalogPrototype.unit;

import com.booksCatalogPrototype.model.Author;
import com.booksCatalogPrototype.repository.AuthorRepository;
import com.booksCatalogPrototype.repository.BookRepository;
import com.booksCatalogPrototype.service.AuthorServiceImpl;
import com.booksCatalogPrototype.unit.util.GetObjects;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AuthorServiceTests {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    AuthorServiceImpl authorService;

    @After
    public void tearDown() throws Exception {
        authorRepository = null;
        bookRepository = null;
        mongoOperations = null;
    }

    @Test
    public void saveTest() {
        Author author = GetObjects.getAuthor();
        authorService.save(author);
        verify(authorRepository, times(1)).save(any());
    }

    @Test
    public void getAllTest() {
        when(authorRepository.findAll()).thenReturn(GetObjects.getAuthorList(4));
        Assert.assertEquals(4, authorService.getAll().size());
    }

    @Test
    public void getAllPageableTest() {
        PageRequest pageRequest = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "firstName"));
        authorService.getAll(pageRequest);
        verify(authorRepository, times(1)).findAll(pageRequest);
        int listSize = 10;
        Page<Author> pagedResponse = new PageImpl<>(GetObjects.getAuthorList(listSize));
        when(authorRepository.findAll(pageRequest)).thenReturn(pagedResponse);
        Assert.assertEquals(listSize, authorService.getAll(pageRequest).getTotalElements());
    }

    @Test
    public void get() {
        Author author = GetObjects.getFilledAuthor();
        when(authorRepository.findById("1")).thenReturn(Optional.of(author));
        Assert.assertEquals("1", authorService.get("1").getId());
    }

    @Test
    public void delete() {
        Author author = GetObjects.getAuthor();
        authorService.delete(author);
        verify(authorRepository, times(1)).delete(any());
    }

//    @Test
//    public void update() {
//        Author author = GetObjects.getAuthor("first", "second");
//        when(authorRepository.findById(null)).thenReturn(Optional.of(author));
//        authorService.update(GetObjects.getAuthor());
//        verify(authorRepository, times(2)).findById(any());
//        Assert.assertEquals(author, authorService.update(author));
//    }

    @Test
    public void deleteAuthors() {
        String[] ids = {"1","2","2","4"};
        when(authorRepository.findById("1")).thenReturn(Optional.of(GetObjects.getAuthor("1")));
        when(authorRepository.findById("2")).thenReturn(Optional.of(GetObjects.getAuthor("2")));
        when(authorRepository.findById("4")).thenReturn(Optional.of(GetObjects.getAuthor("4")));
        authorService.deleteAuthors(ids);
        verify(authorRepository, times(6)).findById(any());
        verify(authorRepository, times(3)).delete(any());
    }
}
