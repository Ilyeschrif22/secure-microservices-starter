package com.esprit.rabbit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HotelEventsRabbitListener {

    private static final Log logger = LogFactory.getLog(HotelEventsRabbitListener.class);

    @RabbitListener(queues = "hotel.events")
    public void onMessage(String message) {
        logger.info("Received hotel event from RabbitMQ: " + message);
    }
}

