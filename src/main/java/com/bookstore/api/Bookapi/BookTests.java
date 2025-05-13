package com.bookstore.api.Bookapi;

import com.bookstore.api.BooksApi;
import com.bookstore.models.Book.BookApi;
import com.bookstore.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

public class BookTests extends TestBase {
    private String authToken;
    private Book testBook;
    private int createdBookId;
    
    @BeforeClass
    public void setup() {
        authToken = AuthApi.authenticate(
            ConfigManager.getAdminUsername(),
            ConfigManager.getAdminPassword()
        ).then().extract().path("token");
        
        testBook = TestDataGenerator.generateBookData();
    }
    
    @Test(priority = 1, description = "Verify book creation")
    public void testCreateBook() {
        Response response = BookApi.createBook(authToken, testBook);
        
        response.then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo(testBook.getTitle()))
            .body("author", equalTo(testBook.getAuthor()));
        
        createdBookId = response.jsonPath().getInt("id");
    }
    
    @Test(priority = 2, description = "Verify book retrieval")
    public void testGetBookById() {
        BookApi.getBookById(authToken, createdBookId)
            .then()
            .statusCode(200)
            .body("id", equalTo(createdBookId))
            .body("title", equalTo(testBook.getTitle()));
    }
    
    @Test(priority = 3, description = "Verify book update")
    public void testUpdateBook() {
        testBook.setTitle("Updated " + testBook.getTitle());
        testBook.setAuthor("Updated Author");
        
        BookApi.updateBook(authToken, createdBookId, testBook)
            .then()
            .statusCode(200)
            .body("title", equalTo(testBook.getTitle()))
            .body("author", equalTo(testBook.getAuthor()));
    }
    
    @Test(priority = 4, description = "Verify book deletion")
    public void testDeleteBook() {
        BookApi.deleteBook(authToken, createdBookId)
            .then()
            .statusCode(204);
        
        BookApi.getBookById(authToken, createdBookId)
            .then()
            .statusCode(404);
    }
    
    @Test(description = "Verify unauthorized access")
    public void testUnauthorizedAccess() {
        BookApi.getAllBooks("invalid_token")
            .then()
            .statusCode(401);
    }
}