package nl.tudelft.wdm.group1.rest;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.payload.UserCreatePayload;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        }
)
public class RestAddUserTest {
    @Autowired
    private MockMvc mockMvc;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, RestTopics.getTopics());

    @Test
    public void userCreateSuccess() throws Exception {
        WdmKafkaTestHelpers.<UserCreatePayload, KafkaResponse<User>>setupKafkaResponse(
                embeddedKafka.getEmbeddedKafka(),
                record -> record.getFirstName().equals("Jane"),
                record -> new KafkaResponse<>(record.getRequestId(), new User("Jane", "Da", "Main Street", "90101", "Rome"))
        );

        MvcResult mvcResult = mockMvc.perform(
                post("/users")
                        .param("firstName", "Jane")
                        .param("lastName", "Da")
                        .param("street", "Main Street")
                        .param("zip", "90101")
                        .param("city", "Rome"))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Da")))
                .andExpect(jsonPath("$.zip", is("90101")))
                .andExpect(status().isOk());
    }

    @Test
    public void userCreateFailure() throws Exception {
        WdmKafkaTestHelpers.<UserCreatePayload, KafkaErrorResponse>setupKafkaResponse(
                embeddedKafka.getEmbeddedKafka(),
                record -> record.getFirstName().equals("John"),
                record -> new KafkaErrorResponse(record.getRequestId(), new ResourceNotFoundException("Cannot create user"))
        );

        MvcResult mvcResult = mockMvc.perform(
                post("/users")
                        .param("firstName", "John")
                        .param("lastName", "Da")
                        .param("street", "Main Street")
                        .param("zip", "90101")
                        .param("city", "Rome"))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().is4xxClientError());
    }
}
