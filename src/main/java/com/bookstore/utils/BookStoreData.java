package data;

import io.restassured.response.Response;
import lombok.Data;

import java.util.List;

@Data
public class BookStoreData {
    private String validEmailUsed;
    private String validPasswordUsed;
    private String accessToken;
    private Response signUpResponse;
    private Response logInResponse;
    private Response addBookResponse;
    private Response editBookResponse;
    private Response getBookDetailsById;
    private List<Response> fetchAllBooks;
    private Response deleteBookResponse;

}
