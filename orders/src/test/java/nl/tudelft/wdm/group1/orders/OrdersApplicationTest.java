package nl.tudelft.wdm.group1.orders;

import com.jayway.jsonpath.JsonPath;
import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
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
public class OrdersApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "orders");

    private Order defaultOrder;
    private UUID defaultOrderItemId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        defaultOrder = new Order(UUID.randomUUID());
        defaultOrder.addItem(defaultOrderItemId);
        orderRepository.add(defaultOrder);
        assertThat(orderRepository.find(defaultOrder.getId())).isNotNull();
    }

    @Test
    public void createNewOrder() throws Exception {
        MvcResult result = this.mockMvc.perform(
                post("/orders/" + defaultOrder.getUserId())
        ).andExpect(status().isOk()).andReturn();

        Thread.sleep(5000); // TODO: Remove this ugly hack

        Order order = orderRepository.find(UUID.fromString(getJsonValue(result, "$.id")));

        assertThat(order.getUserId()).isEqualTo(defaultOrder.getUserId());
        assertThat(order.getItemIds()).isEmpty();
    }

    @Test
    public void retrieveAOrder() throws Exception {
        this.mockMvc.perform(get("/orders/" + defaultOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultOrder.getId().toString())));
    }

    @Test
    public void removeAnOrder() throws Exception {
        this.mockMvc.perform(delete("/orders/" + defaultOrder.getId()))
                .andExpect(status().isOk());

        Thread.sleep(2000); // TODO: Remove this ugly hack

        assertThatThrownBy(() -> orderRepository.find(defaultOrder.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void addAnItemToAnOrder() throws Exception {
        UUID newItemId = UUID.randomUUID();
        this.mockMvc.perform(
                post("/orders/" + defaultOrder.getId() + "/items")
                        .param("itemId", newItemId.toString())
        ).andExpect(status().isOk());

        Thread.sleep(2000); // TODO: Remove this ugly hack

        assertThat(defaultOrder.getItemIds()).contains(defaultOrderItemId, newItemId);
    }

    @Test
    public void removeAnItemFromAnOrder() throws Exception {
        this.mockMvc.perform(
                delete("/orders/" + defaultOrder.getId() + "/items")
                        .param("itemId", defaultOrderItemId.toString())
        ).andExpect(status().isOk());

        Thread.sleep(2000); // TODO: Remove this ugly hack

        assertThat(defaultOrder.getItemIds()).isEmpty();
    }

    private String getJsonValue(MvcResult mvcResult, String path) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return JsonPath.parse(response).read(path).toString();
    }
}
