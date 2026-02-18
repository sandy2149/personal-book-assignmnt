package com.example.demo;

import com.example.demo.constants.ErrorConstants;
import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.exception.CustomBusinessException;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * REST Controller for managing personal book list and Google Books integration.
 */
@RestController
public class BookController {
    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;

    /**
     * Constructor-based dependency injection.
     *
     * @param bookRepository       repository for persisting books
     * @param googleBookService    service for interacting with Google Books API
     */
    @Autowired
    public BookController(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    /**
     * Returns all persisted books from H2 database.
     *
     * @return list of books
     */
    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Searches Google Books API and returns upstream Google schema.
     *
     * @param query      search query
     * @param maxResults optional max results
     * @param startIndex optional start index
     * @return GoogleBook response
     */
    @GetMapping("/google")
    public GoogleBook searchGoogleBooks(@RequestParam("q") String query,
                                        @RequestParam(value = "maxResults", required = false) Integer maxResults,
                                        @RequestParam(value = "startIndex", required = false) Integer startIndex) {
        return googleBookService.searchBooks(query, maxResults, startIndex);
    }

    /**
     * Adds a book from Google Books API to personal list using Google volume ID.
     *
     * @param googleId Google Books volume ID
     * @return persisted Book entity
     */
    @PostMapping("/books/{googleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@PathVariable String googleId) {

        if (bookRepository.existsById(googleId)) {
            throw new CustomBusinessException(ErrorConstants.BOOK_ALREADY_EXISTS);
        }

        GoogleBook.Item item = googleBookService.getBookById(googleId);

        if (item == null || item.volumeInfo() == null) {
            throw new CustomBusinessException(ErrorConstants.BOOK_NOT_FOUND);
        }

        GoogleBook.VolumeInfo volumeInfo = item.volumeInfo();

        String author = extractAuthor(volumeInfo);

        Book book = new Book(
                item.id(),
                volumeInfo.title(),
                author,
                volumeInfo.pageCount()
        );

        return bookRepository.save(book);
    }

    /**
     * Extracts first author safely from Google VolumeInfo.
     */
    private String extractAuthor(GoogleBook.VolumeInfo volumeInfo) {

        if (volumeInfo.authors() == null || volumeInfo.authors().isEmpty()) {
            return null;
        }

        return volumeInfo.authors().get(0);
    }

}
