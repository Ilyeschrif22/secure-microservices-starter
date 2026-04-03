package com.esprit.service;

import com.esprit.entities.Hotel;
import com.esprit.repository.HotelRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;

    private static final String HOTEL_KAFKA_TOPIC = "hotel-events";

    public HotelService(HotelRepository hotelRepository,
                        KafkaTemplate<String, String> kafkaTemplate,
                        RabbitTemplate rabbitTemplate) {
        this.hotelRepository = hotelRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public Hotel createHotel(Hotel hotel) {
        Hotel saved = hotelRepository.save(hotel);

        // Send a simple Kafka event
        String message = "Hotel created: id=" + saved.getId() + ", name=" + saved.getName();
        kafkaTemplate.send(HOTEL_KAFKA_TOPIC, message);

        // Send a RabbitMQ message
        rabbitTemplate.convertAndSend(
                com.esprit.config.MessagingConfig.HOTEL_EXCHANGE,
                com.esprit.config.MessagingConfig.HOTEL_ROUTING_KEY,
                message
        );

        return saved;
    }

    public Optional<Hotel> updateHotel(Long id, Hotel updated) {
        return hotelRepository.findById(id).map(hotel -> {
            hotel.setName(updated.getName());
            hotel.setCity(updated.getCity());
            hotel.setAddress(updated.getAddress());
            hotel.setPricePerNight(updated.getPricePerNight());
            hotel.setRating(updated.getRating());
            hotel.setAvailable(updated.isAvailable());
            return hotelRepository.save(hotel);
        });
    }

    public boolean deleteHotel(Long id) {
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Hotel> getAvailableHotels() {
        return hotelRepository.findByAvailableTrue();
    }

    public List<Hotel> getHotelsByCity(String city) {
        return hotelRepository.findByCityIgnoreCase(city);
    }
}