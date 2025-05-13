package Steps;

	
import api.UserApi;
import data.BookStoreData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserStepDefs {
    HashMap<String,Object> bookDetails=new HashMap<>();
    List<HashMap<String,Object>> allBooksList=new ArrayList<>();
    private final BookStoreData bookStoreData;

    public UserStepDefs()
    {
        this.bookStoreData=new BookStoreData();
    }

    @Given("^Sign up to the book store as the new user with email and password$")
    public void SignUpToTheBookStoreAsTheNewUserWithValidEmailAndPassword()
    {

    }

    @When("^do the sign up with (.*) credentials$")
    public void doTheSignUpWithValidCredentials(String condition) {
        if(condition.equalsIgnoreCase("valid"))
        {
            bookStoreData.setValidEmailUsed(UserApi.generateEmailAndPassword(10)+"@gmail.com");
            bookStoreData.setValidPasswordUsed(UserApi.generateEmailAndPassword(8));
        } else if (condition.equalsIgnoreCase("newPasswordOnly")) {
            bookStoreData.setValidPasswordUsed(UserApi.generateEmailAndPassword(8));
        }

        Response response=UserApi.signUp(bookStoreData.getValidEmailUsed(),bookStoreData.getValidPasswordUsed(), bookStoreData);
        bookStoreData.setSignUpResponse(response);
    }

    @Then("^validate that the response code is (.*) and response message should be (.*) after sign up$")
    public void validateThatTheResponseCodeIsAfterSuccessfulSignUp(int statusCode,String responseMsg) {
        Assert.assertEquals(bookStoreData.getSignUpResponse().getStatusCode(), statusCode,"Sign up expected status code mismatch");
        if(statusCode==200){
            Assert.assertEquals(responseMsg,bookStoreData.getSignUpResponse().getBody().jsonPath().get("message").toString(),"User is not created");
        }
        else if(statusCode==400)
        {
            Assert.assertEquals(responseMsg,bookStoreData.getSignUpResponse().getBody().jsonPath().get("detail").toString(),"There is no error thrown");

        }
    }

    @When("^do the sign up with credentials of email (.*) and password (.*)$")
    public void doTheSignUpWithCredentialsOfEmailEmailAndPasswordPassword(String email,String password) {
        UserApi.signUp(email,password, bookStoreData);
    }

    @When("^user tried to login with (.*) credentials into book store system$")
    public void userTriedToLoginWithValidCredentialsIntoBookStoreSystem(String condition) {
        if(condition.equalsIgnoreCase("noSignUpUser"))
        {
            bookStoreData.setValidEmailUsed(UserApi.generateEmailAndPassword(10)+"@gmail.com");
            bookStoreData.setValidPasswordUsed(UserApi.generateEmailAndPassword(8));
        }
        else if(condition.equalsIgnoreCase("missingParam"))
        {
            bookStoreData.setValidEmailUsed(null);
            bookStoreData.setValidPasswordUsed(null);
        }
        bookStoreData.setLogInResponse(UserApi.login(bookStoreData.getValidEmailUsed(),bookStoreData.getValidPasswordUsed()));



    }

    @Then("^verify the response after login into book store should (.*) and (.*)$")
    public void verifyTheResponseAfterLoginIntoBookStoreShouldAndSuccess(int statusCode,String condition) {
        Assert.assertEquals(bookStoreData.getLogInResponse().getStatusCode(), statusCode,"The response code is not "+statusCode);
        switch (condition)
        {
            case "successLogin":
                bookStoreData.setAccessToken("Bearer "+bookStoreData.getLogInResponse().jsonPath().get("access_token"));
                Assert.assertNotNull(bookStoreData.getLogInResponse().jsonPath().get("access_token"),"Token is not generated after login");
                Assert.assertEquals(bookStoreData.getLogInResponse().jsonPath().get("token_type"),"bearer","Token generated type is not bearer");
                break;

            case "incorrectCredentials":
                Assert.assertEquals(bookStoreData.getLogInResponse().getStatusLine(),"HTTP/1.1 400 Bad Request","Response line is not as expected");
                Assert.assertEquals(bookStoreData.getLogInResponse().jsonPath().get("detail"),"Incorrect email or password","Incorrect error message in detail mismatch");
                break;

            case "missingParam":
                Assert.assertEquals(bookStoreData.getLogInResponse().getStatusLine(),"HTTP/1.1 422 Unprocessable Entity","Response line is not as expected");
                Assert.assertEquals(bookStoreData.getLogInResponse().jsonPath().get("detail.get(0).type"),"missing","Missing param error type is not shown");
                Assert.assertEquals(bookStoreData.getLogInResponse().jsonPath().get("detail.get(0).msg"),"Field required","Field required error should be shown");
                break;

        }
    }


    @Given("^Adding the new book into the store after successful login of user into the system$")
    public void AddingTheNewBookIntoTheStoreAfterSuccessfulLoginOfUserIntoTheSystem()
    {
        Long uniqueIdentifier=System.nanoTime();
        bookDetails.put("bookName", "Book Title "+uniqueIdentifier);
        bookDetails.put("author","Book Author "+uniqueIdentifier);
        bookDetails.put("published_year",uniqueIdentifier);
        bookDetails.put("book_summary","Book summary for the book "+uniqueIdentifier);
        allBooksList.add(new HashMap<>(bookDetails));
    }


    @When("add new book into book store with valid login token of user")
    public void addNewBookIntoBookStoreWithValidLoginTokenOfUser() {
      bookStoreData.setAddBookResponse(BooksApi.addNewBook(bookDetails,bookStoreData.getAccessToken(),bookStoreData));
    }

    @Then("^verify the response after adding the new book should be (.*)$")
    public void verifyTheResponseAfterAddingTheNewBookShouldBeSuccess(String condition) {
        if(condition.equalsIgnoreCase("success"))
        {
            Assert.assertNotNull(bookStoreData.getAddBookResponse().getBody().jsonPath().get("id"),"Unique id is not generated");
            bookDetails.put("createdBookId",bookStoreData.getAddBookResponse().getBody().jsonPath().get("id"));
            Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("name"),bookDetails.get("bookName"),"Book name  mismatch");
            Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("author"),bookDetails.get("author"),"Author name mismatch");
            Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("published_year"),bookDetails.get("published_year"),"Published year mismatch");
            Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("book_summary"),bookDetails.get("book_summary"),"Book summary  mismatch");

        }


    }

    @When("^edit the (.*) of the book added and verify the response after update$")
    public void editTheNameOfTheBookAddedAndVerifyTheResponseAfterUpdate(String editAction) {
        if(editAction.equalsIgnoreCase("name"))
        {
            bookDetails.put("bookName","Book name is edited now");
        } else if (editAction.equalsIgnoreCase("author")) {
            bookDetails.put("author","Book author name is edited now");
        } else if (editAction.equalsIgnoreCase("bookSummary")) {
            bookDetails.put("book_summary","Book summary is edited now via update");
        } else if (editAction.equalsIgnoreCase("published_year")) {
            bookDetails.put("published_year",System.nanoTime());

        }
        if(editAction.equalsIgnoreCase("noAccessToken")){
            bookStoreData.setEditBookResponse(BooksApi.editTheBook(bookDetails,null));
        }
        else {
            bookStoreData.setEditBookResponse(BooksApi.editTheBook(bookDetails,bookStoreData.getAccessToken()));
        }


    }

    @Then("^verify the response after update should be (.*)$")
    public void verifyTheResponseAfterUpdateShouldBe(int statusCode) {
        Assert.assertEquals(bookStoreData.getLogInResponse().getStatusCode(), statusCode,"The response code is not "+statusCode);
        if(statusCode==200)
        {
            Assert.assertEquals(bookStoreData.getEditBookResponse().getStatusLine(),"HTTP/1.1 200 OK","Response line is not as expected for 200");
        }
        else if(statusCode==400)
        {
            Assert.assertEquals(bookStoreData.getEditBookResponse().getStatusLine(),"HTTP/1.1 400 Bad Request","Response line is not as expected for 400");
        } else if (statusCode==403) {
            Assert.assertEquals(bookStoreData.getEditBookResponse().getStatusLine(),"HTTP/1.1 403 Forbidden","Response line is not as expected for 403");
            Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("detail"),"Not authenticated","No error message for 403");
        }
    }

    @And("^verify the edited book details values in response for editing (.*)$")
    public void verifyTheEditedBookDetailsValuesInResponseForEditingName(String editedAction)
    {
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("name"),bookDetails.get("bookName"),"Book name  mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("author"),bookDetails.get("author"),"Author name mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("published_year"),bookDetails.get("published_year"),"Published year mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("book_summary"),bookDetails.get("book_summary"),"Book summary  mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("id"),bookDetails.get("createdBookId"),"Book id is different to the request");
    }

    @When("get the details of the particular book using book id generated while creating")
    public void getTheDetailsOfTheParticularBookUsingBookIdGeneratedWhileCreating() {
      bookStoreData.setGetBookDetailsById(BooksApi.getBookDetailsById(bookDetails, bookStoreData.getAccessToken()));

    }

    @Then("verify the book details are fetched properly in the response by book id")
    public void verifyTheBookDetailsAreFetchedProperlyInTheResponseByBookId() {
        Assert.assertEquals(bookStoreData.getGetBookDetailsById().getBody().jsonPath().get("name"),bookDetails.get("bookName"),"Book name  mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsById().getBody().jsonPath().get("author"),bookDetails.get("author"),"Author name mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsById().getBody().jsonPath().get("published_year"),bookDetails.get("published_year"),"Published year mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsById().getBody().jsonPath().get("book_summary"),bookDetails.get("book_summary"),"Book summary  mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsById().getBody().jsonPath().get("id"),bookDetails.get("createdBookId"),"Book id is different to the request");

    }

    @Then("verify the book details should not be fetched properly in the response for deleted book id")
    public void verifyTheBookDetailsShouldNotBeFetchedProperlyInTheResponseForDeletedBookId() {
        Assert.assertEquals(bookStoreData.getGetBookDetailsById().getBody().jsonPath().get("detail"),"Book not found","Book details should not be fetched for deleted");

    }

    @When("fetch all the books that added to the book store")
    public void fetchAllTheBooksThatAddedToTheBookStore() {
       bookStoreData.setFetchAllBooks(BooksApi.getAllBooks(bookStoreData.getAccessToken()));
    }

    @Then("verify the details of books that listed")
    public void verifyTheDetailsOfBooksThatListed() {
        for(HashMap<String,Object> eachData:allBooksList)
        {
            System.out.println(bookStoreData.getFetchAllBooks().contains(eachData));
        }
    }

    @And("delete the added book in the book store using book id and verify the response")
    public void deleteTheAddedBookInTheBookStoreUsingBookIdAndVerifyTheResponse() {
       bookStoreData.setDeleteBookResponse(BooksApi.deleteTheBookById(bookDetails.get("createdBookId").toString(), bookStoreData.getAccessToken()));
    }

    @And("^verify the response after deleting the book should be (.*)$")
    public void verifyTheResponseAfterDeletingTheBookShouldBeSuccess(String condition) {
        if(condition.equalsIgnoreCase("Success"))
        {
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusCode(), 200,"The response code is not 200");
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusLine(),"HTTP/1.1 200 OK","Response line is not as expected");
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getBody().jsonPath().get("message"),"Book deleted successfully","Book not deleted yet");
        } else if (condition.equalsIgnoreCase("notFound")) {
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusCode(), 404,"The response code is not 404");
        }
    }

}
