package nl.tudelft.wdm.group1.stock;

import nl.tudelft.wdm.group1.common.exception.InsufficientStockException;
import nl.tudelft.wdm.group1.common.exception.InvalidStockChangeException;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.Order;
import nl.tudelft.wdm.group1.common.model.StockItem;
import nl.tudelft.wdm.group1.stock.events.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        }
)
@DirtiesContext
public class StockConsumerMultiThreadTest {
    @Autowired
    private StockItemRepository stockItemRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "stock");

    @Autowired
    private Consumer stockConsumer;

    private StockItem stockItem1;
    private StockItem stockItem2;
    private StockItem stockItem3;
    private StockItem stockItem4;
    private StockItem stockItem5;

    private int numThreads;

    @Before
    public void setUp() {
        // Setup the stock items to have the same amount of items in stock as the number threads that will be executed.
        numThreads = 4;

        stockItem1 = new StockItem(numThreads, "Stock1", 1);
        stockItem2 = new StockItem(numThreads, "Stock2", 1);
        stockItem3 = new StockItem(numThreads, "Stock3", 1);
        stockItem4 = new StockItem(numThreads, "Stock4", 1);
        stockItem5 = new StockItem(numThreads, "Stock5", 1);

        stockItemRepository.save(stockItem1);
        stockItemRepository.save(stockItem2);
        stockItemRepository.save(stockItem3);
        stockItemRepository.save(stockItem4);
        stockItemRepository.save(stockItem5);
    }

    @Test
    public void testMultiThreadInStock() throws ResourceNotFoundException, InterruptedException {
        executeCheckoutInParallel();

        // Every stock should be empty.
        assertThat(stockItemRepository.findOrElseThrow(stockItem1.getId()).getStock()).isEqualTo(0);
        assertThat(stockItemRepository.findOrElseThrow(stockItem2.getId()).getStock()).isEqualTo(0);
        assertThat(stockItemRepository.findOrElseThrow(stockItem3.getId()).getStock()).isEqualTo(0);
        assertThat(stockItemRepository.findOrElseThrow(stockItem4.getId()).getStock()).isEqualTo(0);
        assertThat(stockItemRepository.findOrElseThrow(stockItem5.getId()).getStock()).isEqualTo(0);
    }

    @Test
    public void testMultiThreadOutOfStock() throws InterruptedException, ResourceNotFoundException, InvalidStockChangeException, InsufficientStockException {
        // When one item has one stock less the execution will fail at least once.
        stockItem1.subtractStock(1);
        stockItemRepository.save(stockItem1);

        executeCheckoutInParallel();

        // Only the first stock should be empty.
        assertThat(stockItemRepository.findOrElseThrow(stockItem1.getId()).getStock()).isEqualTo(0);
        assertThat(stockItemRepository.findOrElseThrow(stockItem2.getId()).getStock()).isEqualTo(1);
        assertThat(stockItemRepository.findOrElseThrow(stockItem3.getId()).getStock()).isEqualTo(1);
        assertThat(stockItemRepository.findOrElseThrow(stockItem4.getId()).getStock()).isEqualTo(1);
        assertThat(stockItemRepository.findOrElseThrow(stockItem5.getId()).getStock()).isEqualTo(1);
    }

    private void executeCheckoutInParallel() throws InterruptedException {
        // Creates an order and triggers checkout.
        Runnable checkoutOrderRunnable = () -> {
            Order order2 = new Order(UUID.randomUUID());
            order2.addItem(stockItem1.getId());
            order2.addItem(stockItem2.getId());
            order2.addItem(stockItem3.getId());
            order2.addItem(stockItem4.getId());
            order2.addItem(stockItem5.getId());

            try {
                stockConsumer.consumeOrderCheckedOut(order2);
            } catch (ResourceNotFoundException | InvalidStockChangeException | InsufficientStockException e) {
                // This case should not be possible.
                Assert.fail();
            }
        };

        // Run the checkoutOrderRunnable numThreads times.
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        for (Runnable runnable : Collections.nCopies(numThreads, checkoutOrderRunnable)) {
            pool.execute(runnable);
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
