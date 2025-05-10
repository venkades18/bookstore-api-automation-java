package com.bookstore.utils;

import java.nio.file.DirectoryStream.Filter;

import com.bookstore.models.Book.BookApi;
import com.github.javafaker.Faker;

public class TestDataGenerator {
    private static final Filter faker = new Faker();
    
    public static Book generateBookData() {
        Book book = new Book();
        book.setTitle(faker.book().title());
        book.setAuthor(faker.book().author());
        book.setIsbn(faker.code().isbn10());
        book.setPrice(faker.number().randomDouble(2, 10, 100));
        book.setQuantity(faker.number().numberBetween(1, 100));
        return book;
    }
}