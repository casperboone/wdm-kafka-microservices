package nl.tudelft.wdm.group1.endToEnd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class EndToEndTest {

    private static final String DEFAULT_BASE_URI = "http://localhost:8080";
    private static final String ENVIRONMENT_VARIABLE_BASE_URI = "END_TO_END_HOST";
    private static final String PROTOCOL_HTTP = "http";
    private static final String PROTOCOL_HTTPS = "https";

    @Before
    public void setUp() throws MalformedURLException {
        String baseURI = System.getenv(ENVIRONMENT_VARIABLE_BASE_URI);
        if (baseURI == null) {
            baseURI = DEFAULT_BASE_URI;
        }

        URL baseURL = new URL(baseURI);

        if (baseURL.getPort() == -1) {
            if (PROTOCOL_HTTP.equals(baseURL.getProtocol())) {
                RestAssured.port = 80;
            } else if (PROTOCOL_HTTPS.equals(baseURL.getProtocol())) {
                RestAssured.port = 443;
            } else {
                RestAssured.port = baseURL.getPort();
            }
        }

        RestAssured.baseURI = baseURL.getProtocol() + "://" + baseURL.getHost();
    }

    @Test
    public void createAndDeleteUser() throws InterruptedException {
        JsonPath response = given()
                .param("firstName", "Jane")
                .param("lastName", "Da")
                .param("street", "Main Street")
                .param("zip", "90101")
                .param("city", "Rome")
                .when().post("/users")
                .andReturn().jsonPath();

        assertThat(response.get("firstName"), equalTo("Jane"));
        assertThat(response.get("lastName"), equalTo("Da"));

        Thread.sleep(2000);

        JsonPath listResponse = given()
                .when().get("/users/" + response.get("id"))
                .andReturn().jsonPath();

        assertThat(listResponse.get("firstName"), equalTo("Jane"));
        assertThat(listResponse.get("id"), equalTo(response.get("id")));

        given()
                .when().delete("/users/" + response.get("id"))
                .then().statusCode(200);

        Thread.sleep(2000);

        given()
                .when().get("/users/" + response.get("id"))
                .then().statusCode(404);
    }
}
