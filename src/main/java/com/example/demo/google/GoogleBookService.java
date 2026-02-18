package com.example.demo.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoogleBookService {
    private final RestClient restClient;

    public GoogleBookService(@Value("${google.books.base-url:https://www.googleapis.com/books/v1}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public GoogleBook searchBooks(String query, Integer maxResults, Integer startIndex) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("maxResults", maxResults != null ? maxResults : 10)
                        .queryParam("startIndex", startIndex != null ? startIndex : 0)
                        .build())
                .retrieve()
                .body(GoogleBook.class);
    }


    /**
     * Retrieves a book from the Google Books API using its unique identifier.
     *
     * <p>This method sends a GET request to the Google Books API endpoint
     * {@code /volumes/{id}} and attempts to retrieve the book details
     * corresponding to the provided ID. The response is deserialized into
     * a {@link GoogleBook.Item} object.</p>
     *
     * @param id the unique identifier of the book volume in the Google Books API
     * @return a {@link GoogleBook.Item} representing the book details
     *         associated with the given ID, or {@code null} if no book is found
     *
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException
     *         if the API call fails or returns an error response
     * @throws IllegalArgumentException if the provided {@code id} is null or empty
     *
     * @see GoogleBook.Item
     * @see <a href="https://developers.google.com/books/docs/v1/reference/volumes/get">
     *      Google Books API - Volumes: get</a>
     */
    public GoogleBook.Item getBookById(String id) {
        return restClient.get()
                .uri("/volumes/{id}", id)
                .retrieve()
                .body(GoogleBook.Item.class);
    }


}

