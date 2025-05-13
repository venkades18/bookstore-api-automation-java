package Hooks;
import io.cucumber.java.Before;

import static io.restassured.RestAssured.baseURI;

public class CucumberHooks {
    @Before
    public  void setup() {
        baseURI="http://127.0.0.1:8000";
    }
}
