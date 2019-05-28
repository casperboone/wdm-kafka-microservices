package nl.tudelft.wdm.group1.endtoend;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

        Awaitility.setDefaultPollInterval(500, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultPollDelay(100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void createAndDeleteUser() {
        List<UUID> users = createUsers();
        List<UUID> stocks = createStocks();

        addCredit(users.get(0), 3000);
        addCredit(users.get(1), 4000);

        UUID order = createOrder(users.get(0));

        addOrderItem(order, stocks.get(0));
        addOrderItem(order, stocks.get(1));

        deleteOrderItem(order, stocks.get(0));

        addOrderItem(order, stocks.get(2));

        checkoutOrder(order);

        deleteUsers(users);
    }

    private void addCredit(UUID userId, int amount) {
        Response response = given()
                .when().get("/users/" + userId + "/credit");

        response.then().statusCode(200);

        int currentAmount = response.jsonPath().get("credit");

        given()
                .when().post("/users/" + userId + "/credit/add/" + amount)
                .then().statusCode(200);

        await().until(() -> given()
                .when().get("/users/" + userId + "/credit")
                .jsonPath().get("credit").equals(currentAmount + amount));
    }

    private void checkoutOrder(UUID order) {
        given()
                .when().post("/orders/" + order + "/checkout")
                .then().statusCode(200);
    }

    private void deleteOrderItem(UUID order, UUID itemId) {
        given().param("itemId", itemId)
                .when().delete("/orders/" + order + "/items")
                .then().statusCode(200);
    }

    private void addOrderItem(UUID order, UUID itemId) {
        given().param("itemId", itemId)
                .when().post("/orders/" + order + "/items")
                .then().statusCode(200);
    }

    private UUID createOrder(UUID userId) {
        Response response = given()
                .when().post("/orders/" + userId)
                .andReturn();

        response.then().statusCode(200);

        UUID id = UUID.fromString(response.jsonPath().get("id"));

        await().untilAsserted(() -> given().when().get("/orders/" + id).then().statusCode(200));

        return id;
    }

    private List<UUID> createUsers() {
        List<Map<String, String>> users = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("firstName", "Jane");
                put("lastName", "Da");
                put("street", "Main Street");
                put("zip", "90101");
                put("city", "Rome");
            }});
            add(new HashMap<String, String>() {{
                put("firstName", "John");
                put("lastName", "Doe");
                put("street", "Second Street");
                put("zip", "90102");
                put("city", "Pisa");
            }});
        }};

        List<UUID> userIds = new ArrayList<>();

        for (Map<String, String> user : users) {
            Response response = given()
                    .params(user)
                    .when().post("/users")
                    .andReturn();

            response.then().statusCode(200);

            UUID id = UUID.fromString(response.jsonPath().get("id"));

            await().untilAsserted(() -> given().when().get("/users/" + id).then().statusCode(200));

            userIds.add(id);
        }

        return userIds;
    }

    private List<UUID> createStocks() {
        List<Map<String, String>> stocks = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("stock", "30");
                put("name", "stock_1");
                put("price", "20");
            }});
            add(new HashMap<String, String>() {{
                put("stock", "40");
                put("name", "stock_2");
                put("price", "21");
            }});
            add(new HashMap<String, String>() {{
                put("stock", "50");
                put("name", "stock_2");
                put("price", "22");
            }});
        }};

        List<UUID> stockIds = new ArrayList<>();

        for (Map<String, String> stock : stocks) {

            Response stockResponse = given().params(stock)
                    .when().post("/stock").andReturn()
                    .andReturn();

            stockResponse.then().statusCode(200);

            UUID id = UUID.fromString(stockResponse.jsonPath().get("id"));

            await().untilAsserted(() -> given().when().get("/stock/" + id).then().statusCode(200));

            stockIds.add(id);
        }

        return stockIds;
    }

    private void deleteUsers(List<UUID> users) {
        for (UUID user : users) {
            given()
                    .when().delete("/users/" + user)
                    .then().statusCode(200);

            await().untilAsserted(() -> given().when().get("/users/" + user).then().statusCode(404));
        }
    }
}
