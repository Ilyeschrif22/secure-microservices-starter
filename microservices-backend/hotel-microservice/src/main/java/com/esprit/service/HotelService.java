package com.esprit.service;

import com.esprit.entities.Hotel;
import com.esprit.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
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