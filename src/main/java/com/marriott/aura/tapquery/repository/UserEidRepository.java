package com.marriott.aura.tapquery.repository;

import com.marriott.aura.tapquery.document.UserEid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserEidRepository extends MongoRepository<UserEid, String> {

    List<UserEid> findByEid(String eid);

    Optional<UserEid> findByEidAndApp(String eid, String app);

    Page<UserEid> findByApp(String app, Pageable pageable);

    Page<UserEid> findByStatus(String status, Pageable pageable);

    Page<UserEid> findByAppAndStatus(String app, String status, Pageable pageable);

    Page<UserEid> findByLocationsContaining(String locationCode, Pageable pageable);

    Page<UserEid> findByAssignmentsContaining(String assignment, Pageable pageable);

    long countByApp(String app);

    long countByStatus(String status);

    long countByAppAndStatus(String app, String status);
}
