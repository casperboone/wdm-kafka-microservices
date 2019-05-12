package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.stock.StockItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, StockItem> kafkaTemplate;

    public void send(StockItem stockItem) {
        logger.info(String.format("#### -> Producing message -> %s", stockItem));
        this.kafkaTemplate.send(topic, stockItem);
    }
}
