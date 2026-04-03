package com.esprit.kafka;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class HotelEventsListener {

    private static final Log logger = LogFactory.getLog(HotelEventsListener.class);

    @KafkaListener(topics = "hotel-events", groupId = "hotel-listeners")
    public void handleHotelEvent(String message) {
        logger.info("Received hotel event from Kafka: " + message);
    }
}

