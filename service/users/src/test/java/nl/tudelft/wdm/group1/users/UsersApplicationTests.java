package nl.tudelft.wdm.group1.users;

import com.jayway.jsonpath.JsonPath;
import nl.tudelft.wdm.group1.common.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.User;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        }
)
public class UsersApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 1, "users3");

    private User defaultUser;

    @Before
    public void setUp() throws CreditChangeInvalidException {
        defaultUser = new User("John", "Doe", "Mekelweg 4", "2628 CD", "Delft");
        defaultUser.addCredit(2249);
        userRepository.add(defaultUser);
    }

    @Test
    public void createNewUser() throws Exception {
        MvcResult result = this.mockMvc.perform(
                post("/users")
                        .param("firstName", "Jane")
                        .param("lastName", "Da")
                        .param("street", "Main Street")
                        .param("zip", "90101")
                        .param("city", "Rome")
        ).andExpect(status().isOk()).andReturn();

        await().until(() -> userRepository.contains(UUID.fromString(getJsonValue(result, "$.id"))));

        User user = userRepository.find(UUID.fromString(getJsonValue(result, "$.id")));

        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Da");
        assertThat(user.getStreet()).isEqualTo("Main Street");
        assertThat(user.getZip()).isEqualTo("90101");
        assertThat(user.getCity()).isEqualTo("Rome");
        assertThat(user.getCredit()).isEqualTo(0);
    }

    @Test
    public void retrieveAUser() throws Exception {
        this.mockMvc.perform(get("/users/" + defaultUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultUser.getId().toString())))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.street", is("Mekelweg 4")))
                .andExpect(jsonPath("$.zip", is("2628 CD")))
                .andExpect(jsonPath("$.city", is("Delft")))
                .andExpect(jsonPath("$.credit", is(2249)));
    }

    @Test
    public void removeAUser() throws Exception {
        this.mockMvc.perform(delete("/users/" + defaultUser.getId()))
                .andExpect(status().isOk());

        await().untilAsserted(() -> assertThatThrownBy(() -> userRepository.find(defaultUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class));
    }

    @Test
    public void retrieveCredit() throws Exception {
        this.mockMvc.perform(get("/users/" + defaultUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credit", is(2249)));
    }

    @Test
    public void addCredit() throws Exception {
        this.mockMvc.perform(post("/users/" + defaultUser.getId() + "/credit/add/1500"))
                .andExpect(status().isOk());

        assertThat(defaultUser.getCredit()).isEqualTo(3749);
    }

    @Test
    public void addNegativeCreditAmount() throws Exception {
        this.mockMvc.perform(post("/users/" + defaultUser.getId() + "/credit/add/-1500"))
                .andExpect(status().isUnprocessableEntity());

        assertThat(defaultUser.getCredit()).isEqualTo(2249);
    }

    @Test
    public void subtractCreditWhenAvailable() throws Exception {
        this.mockMvc.perform(post("/users/" + defaultUser.getId() + "/credit/subtract/1500"))
                .andExpect(status().isOk());

        assertThat(defaultUser.getCredit()).isEqualTo(749);
    }

    @Test
    public void subtractCreditWhenNotAvailable() throws Exception {
        this.mockMvc.perform(post("/users/" + defaultUser.getId() + "/credit/subtract/3000"))
                .andExpect(status().isUnprocessableEntity());

        assertThat(defaultUser.getCredit()).isEqualTo(2249);
    }

    @Test
    public void subtractNegativeCreditAmount() throws Exception {
        this.mockMvc.perform(post("/users/" + defaultUser.getId() + "/credit/subtract/-1500"))
                .andExpect(status().isUnprocessableEntity());

        assertThat(defaultUser.getCredit()).isEqualTo(2249);
    }

    private String getJsonValue(MvcResult mvcResult, String path) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return JsonPath.parse(response).read(path).toString();
    }
}
