package com.booksCatalogPrototype.unit.util;

import com.booksCatalogPrototype.model.Author;
import com.booksCatalogPrototype.model.Book;
import com.booksCatalogPrototype.model.Publisher;
import com.booksCatalogPrototype.model.Review;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetObjects {

    public static Book getBook(){
        return new Book();
    }

    public static Book getFilledBook(){
        return new Book("1","name", new Date(), new Publisher("publisher"), new Date(), 0.0, 0, null, null);
    }

    public static Book getBook(String name, Date yearPublished, String publisherName){
        return new Book(name, yearPublished, new Publisher(publisherName), null);
    }

    public static Book getBook(String id){
        return new Book(id,"name", new Date(), new Publisher("publisher"), new Date(), 0.0, 0, null, new LinkedList<>());
    }

    public static Book getBook(Review review){
        List<Review> reviews = Arrays.asList(review);
        return new Book("id","name", new Date(), new Publisher("publisher"), new Date(), 0.0, 0, null, reviews);
    }

    public static List<Book> getBooksList(int size){
        return Stream.iterate(0, i -> i).limit(size).map(book -> new Book()).collect(Collectors.toList());
    }

    public static Author getAuthor(){
        return new Author();
    }

    public static Author getAuthor(String id){
        return new Author(id, null, null, null);
    }

    public static Author getAuthor(String firstName, String secondName){
        return new Author(firstName, secondName);
    }

    public static List<Author> getAuthorList(int size){
        return Stream.iterate(0, i -> i).limit(size).map(author -> new Author()).collect(Collectors.toList());
    }

    public static Author getFilledAuthor(){
        return new Author("1", "name", "name", new Date());
    }

    public static Author getAuthorWithId(String id){
        return new Author(id, "name", "name", new Date());
    }

    public static MultipartFile getMultipartFileImage() {
        try {
            File file = new File("C:\\Users\\vtahi\\IdeaProjects\\BooksCatalogPrototype\\src\\test\\java\\com\\softserve\\booksCatalogPrototype\\resources\\book_PNG2121.png");
            InputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "image/png", input);
            input.close();
            return multipartFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Review getReview(){
        return new Review();
    }

    public static Review getFilledReview(){
        return new Review("id", "name", "comment", new ArrayList<>(), new Date());
    }

    public static Review getReview(String name, String comment){
        return new Review(name, comment);
    }

}
