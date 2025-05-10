package com.bookstore.models.Book;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import com.bookstore.models.Book.Book;

public class BookApi {
    private static final String BOOKS_ENDPOINT = "/books";
    private static final String BOOK_BY_ID_ENDPOINT = "/books/{id}";

    public static Response getAllBooks(String token) {
        return given()
                .spec(RequestSpecs.getAuthenticatedSpec(token))
                .when()
                .get(BOOKS_ENDPOINT);
    }
    
    public static Response getBookById(String token, int id) {
        return given()
                .spec(RequestSpecs.getAuthenticatedSpec(token))
                .pathParam("id", id)
                .when()
                .get(BOOK_BY_ID_ENDPOINT);
    }
    
    public static Response createBook(String token, Book book) {
        return given()
                .spec(RequestSpecs.getAuthenticatedSpec(token))
                .body(book)
                .when()
                .post(BOOKS_ENDPOINT);
    }
    
    public static Response updateBook(String token, int id, Book book) {
        return given()
                .spec(RequestSpecs.getAuthenticatedSpec(token))
                .pathParam("id", id)
                .body(book)
                .when()
                .put(BOOK_BY_ID_ENDPOINT);
    }
    
    public static Response deleteBook(String token, int id) {
        return given()
                .spec(RequestSpecs.getAuthenticatedSpec(token))
                .pathParam("id", id)
                .when()
                .delete(BOOK_BY_ID_ENDPOINT);
    }
}