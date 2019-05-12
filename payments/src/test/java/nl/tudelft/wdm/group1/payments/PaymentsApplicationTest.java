package nl.tudelft.wdm.group1.payments;

import com.jayway.jsonpath.JsonPath;
import nl.tudelft.wdm.group1.payments.Payment;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class PaymentsApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment defaultPayment;

    @Before
    public void setUp() {
        defaultPayment = new Payment();
        paymentRepository.add(defaultPayment);
    }

    @Test
    public void createNewPayment() throws Exception {
        MvcResult result = this.mockMvc.perform(
                post("/payments")
        ).andExpect(status().isOk()).andReturn();

        Thread.sleep(2000); // TODO: Remove this ugly hack

        Payment payment = paymentRepository.find(UUID.fromString(getJsonValue(result, "$.id")));

        assertThat(payment).isNotEqualTo("<add useful asserts>");
    }

    @Test
    public void retrieveAPayment() throws Exception {
        this.mockMvc.perform(get("/payments/" + defaultPayment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultPayment.getId().toString())));
    }

    private String getJsonValue(MvcResult mvcResult, String path) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return JsonPath.parse(response).read(path).toString();
    }
}
