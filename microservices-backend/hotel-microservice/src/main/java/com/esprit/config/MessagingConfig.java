package com.esprit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    public static final String HOTEL_QUEUE = "hotel.events";
    public static final String HOTEL_EXCHANGE = "hotel.exchange";
    public static final String HOTEL_ROUTING_KEY = "hotel.created";

    @Bean
    public Queue hotelQueue() {
        return new Queue(HOTEL_QUEUE, true);
    }

    @Bean
    public DirectExchange hotelExchange() {
        return new DirectExchange(HOTEL_EXCHANGE);
    }

    @Bean
    public Binding hotelBinding(Queue hotelQueue, DirectExchange hotelExchange) {
        return BindingBuilder
                .bind(hotelQueue)
                .to(hotelExchange)
                .with(HOTEL_ROUTING_KEY);
    }
}

