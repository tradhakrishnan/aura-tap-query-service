package com.marriott.aura.tapquery.repository;

import com.marriott.aura.tapquery.document.ControlHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ControlHotelRepository extends MongoRepository<ControlHotel, String> {

    Page<ControlHotel> findByStatus(String status, Pageable pageable);

    Page<ControlHotel> findByCrsSystem(String crsSystem, Pageable pageable);

    Page<ControlHotel> findByCrsSystemAndStatus(String crsSystem, String status, Pageable pageable);

    Page<ControlHotel> findByLocationNameRegex(String regex, Pageable pageable);

    List<ControlHotel> findByIdIn(List<String> ids);

    long countByStatus(String status);

    long countByCrsSystem(String crsSystem);

    long countByCrsSystemAndStatus(String crsSystem, String status);
}
