package api;

import constants.EndPoints;
import data.BookStoreData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class BooksApi {


    public static Response addNewBook(HashMap<String,Object> bookDetails, String accessToken,BookStoreData bookStoreData)
    {
        RequestSpecification request = given().contentType(ContentType.JSON).log().all();

        if (accessToken!=null) {
            request.header("Authorization", accessToken);
        }

        if(!bookDetails.isEmpty())
        {
            request.body("{\"name\":\""+bookDetails.get("bookName")+"\",\"author\":\""+bookDetails.get("author")+"\",\"published_year\":\""+bookDetails.get("published_year")+"\",\"book_summary\":\""+bookDetails.get("book_summary")+"\"}");
        }
        return request.when().post(EndPoints.ADD_NEW_BOOK)
                .then().log().all().extract().response();
    }

    public static Response editTheBook(HashMap<String,Object> bookDetails, String accessToken)
    {
        RequestSpecification request = given().contentType(ContentType.JSON).log().all();

        if (accessToken!=null) {
            request.header("Authorization", accessToken);
        }

        if(!bookDetails.isEmpty())
        {
            request.body("{\"name\":\""+bookDetails.get("bookName")+"\",\"author\":\""+bookDetails.get("author")+"\",\"published_year\":\""+bookDetails.get("published_year")+"\",\"book_summary\":\""+bookDetails.get("book_summary")+"\"}");
        }
        return request.pathParam("book_id",bookDetails.get("createdBookId")).when().put(EndPoints.BY_BOOK_ID)
                .then().log().all().extract().response();
    }

    public static Response getBookDetailsById(HashMap<String,Object> bookDetails, String accessToken)
    {
        RequestSpecification request = given().contentType(ContentType.JSON).log().all();

        if (accessToken!=null) {
            request.header("Authorization", accessToken);
        }
        return request.pathParam("book_id",bookDetails.get("createdBookId")).when().get(EndPoints.BY_BOOK_ID)
                .then().log().all().extract().response();
    }

    public static List<Response> getAllBooks(String accessToken)
    {
        List<Response> responses = new ArrayList<>();
        RequestSpecification request = given().contentType(ContentType.JSON).log().all();

        if (accessToken!=null) {
            request.header("Authorization", accessToken);
        }
               Response response= request.when().get(EndPoints.ADD_NEW_BOOK)
                .then().log().all().extract().response();
        return Collections.singletonList(response);
    }


    public static Response deleteTheBookById(String id, String accessToken)
    {
        RequestSpecification request = given().contentType(ContentType.JSON).log().all();

        if (accessToken!=null) {
            request.header("Authorization", accessToken);
        }
        return request.pathParam("book_id",id).when().delete(EndPoints.BY_BOOK_ID)
                .then().log().all().extract().response();
    }
}
