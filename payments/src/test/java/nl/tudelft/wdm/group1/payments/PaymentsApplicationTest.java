package nl.tudelft.wdm.group1.payments;

import com.jayway.jsonpath.JsonPath;
import nl.tudelft.wdm.group1.payments.Payment;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class PaymentsApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "payments");

    private Payment defaultPayment;
    private UUID defaultUserId = UUID.randomUUID();
    private UUID defaultOrderId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        defaultPayment = new Payment(defaultUserId, defaultOrderId);
        paymentRepository.add(defaultPayment);
        assertThat(paymentRepository.find(defaultPayment.getId())).isNotNull();
    }

    @Test
    public void createNewPayment() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        MvcResult result = this.mockMvc.perform(
                post("/payments/" + userId + "/" + orderId)
        ).andExpect(status().isOk()).andReturn();

        Thread.sleep(2000); // TODO: Remove this ugly hack

        UUID newUserId = UUID.fromString(getJsonValue(result, "$.userId"));
        UUID newOrderId = UUID.fromString(getJsonValue(result, "$.orderId"));

        assertThat(newUserId).isEqualTo(userId);
        assertThat(newOrderId).isEqualTo(orderId);
    }

    @Test
    public void deleteAPayment() throws Exception {
        this.mockMvc.perform(delete("/payments/" + defaultUserId + "/" + defaultOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultPayment.getId().toString())));
    }

    @Test
    public void retrieveAPayment() throws Exception {
        this.mockMvc.perform(get("/payments/" + defaultOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultPayment.getId().toString())));
    }

    private String getJsonValue(MvcResult mvcResult, String path) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return JsonPath.parse(response).read(path).toString();
    }
}
