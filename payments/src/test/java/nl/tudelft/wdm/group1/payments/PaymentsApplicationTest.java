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
    private UUID defaultUserItemId = UUID.randomUUID();
    private UUID defaultOrderItemId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        defaultPayment = new Payment(defaultUserItemId, defaultOrderItemId);
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

        Thread.sleep(10000); // TODO: Remove this ugly hack

        Payment payment = paymentRepository.find(UUID.fromString(getJsonValue(result, "$.id")));

        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
    }

    @Test
    public void retrieveAPayment() throws Exception {
        this.mockMvc.perform(get("/payments/" + defaultOrderItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultPayment.getId().toString())));
    }

    private String getJsonValue(MvcResult mvcResult, String path) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return JsonPath.parse(response).read(path).toString();
    }
}
