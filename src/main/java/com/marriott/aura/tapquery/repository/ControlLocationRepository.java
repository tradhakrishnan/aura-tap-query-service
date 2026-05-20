package com.marriott.aura.tapquery.repository;

import com.marriott.aura.tapquery.document.ControlLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ControlLocationRepository extends MongoRepository<ControlLocation, String> {

    Page<ControlLocation> findByApp(String app, Pageable pageable);

    Page<ControlLocation> findByStatus(String status, Pageable pageable);

    Page<ControlLocation> findByAppAndStatus(String app, String status, Pageable pageable);

    Page<ControlLocation> findByControlledHotelsContaining(String hotelCode, Pageable pageable);

    Page<ControlLocation> findBySupervisorEidsContaining(String eid, Pageable pageable);

    Page<ControlLocation> findByLocationNameRegex(String regex, Pageable pageable);

    long countByApp(String app);

    long countByStatus(String status);

    long countByAppAndStatus(String app, String status);
}
