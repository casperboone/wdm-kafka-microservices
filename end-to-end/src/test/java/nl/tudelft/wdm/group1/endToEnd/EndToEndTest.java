package nl.tudelft.wdm.group1.endToEnd;

import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import org.junit.Test;

public class EndToEndTest {
    @Test
    public void createAndDeleteUser() {
        RestAssured
                .given()
                .param("firstName", "Jane")
                .param("lastName", "Da")
                .param("street", "Main Street")
                .param("zip", "90101")
                .param("city", "Rome")
                .when()
                .post("http://localhost:8080/users")
                .then()
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Da"));
    }
}
