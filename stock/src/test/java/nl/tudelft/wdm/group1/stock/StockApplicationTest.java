package nl.tudelft.wdm.group1.stock;

import com.jayway.jsonpath.JsonPath;
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
public class StockApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockItemRepository stockItemRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "stock");

    private StockItem defaultStockItem;

    @Before
    public void setUp() {
        defaultStockItem = new StockItem(100, "item1", 1000);
        stockItemRepository.add(defaultStockItem);
    }

    @Test
    public void createNewStockItem() throws Exception {
        MvcResult result = this.mockMvc.perform(
                post("/stock")
                        .param("stock", "99")
                        .param("name", "item2")
                        .param("price", "999")
        ).andExpect(status().isOk()).andReturn();

        Thread.sleep(2000); // TODO: Remove this ugly hack

        StockItem stockItem = stockItemRepository.find(UUID.fromString(getJsonValue(result, "$.id")));

        assertThat(stockItem.getStock()).isEqualTo(99);
        assertThat(stockItem.getName()).isEqualTo("item2");
        assertThat(stockItem.getPrice()).isEqualTo(999);
    }

    @Test
    public void retrieveAStockItem() throws Exception {
        this.mockMvc.perform(get("/stock/" + defaultStockItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(defaultStockItem.getId().toString())))
                .andExpect(jsonPath("$.stock", is(100)))
                .andExpect(jsonPath("$.name", is("item1")))
                .andExpect(jsonPath("$.price", is(1000)));
    }

    @Test
    public void addStock() throws Exception {
        this.mockMvc.perform(post("/stock/" + defaultStockItem.getId() + "/add/100"))
                .andExpect(status().isOk());

        assertThat(defaultStockItem.getStock()).isEqualTo(200); //100 + 100 = 200
    }

    @Test
    public void addNegativeStockAmount() throws Exception {
        this.mockMvc.perform(post("/stock/" + defaultStockItem.getId() + "/add/-100"))
                .andExpect(status().isUnprocessableEntity());

        assertThat(defaultStockItem.getStock()).isEqualTo(100); //100 remains unchanged
    }

    @Test
    public void subtractStock() throws Exception {
        this.mockMvc.perform(post("/stock/" + defaultStockItem.getId() + "/subtract/10"))
                .andExpect(status().isOk());

        assertThat(defaultStockItem.getStock()).isEqualTo(90); //100 - 10 = 90
    }

    @Test
    public void subtractNegativeStockAmount() throws Exception {
        this.mockMvc.perform(post("/stock/" + defaultStockItem.getId() + "/subtract/-10"))
                .andExpect(status().isUnprocessableEntity());

        assertThat(defaultStockItem.getStock()).isEqualTo(100); //100 remains unchanged
    }

    private String getJsonValue(MvcResult mvcResult, String path) throws UnsupportedEncodingException {
        String response = mvcResult.getResponse().getContentAsString();

        return JsonPath.parse(response).read(path).toString();
    }
}
