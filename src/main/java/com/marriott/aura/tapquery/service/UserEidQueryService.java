package com.marriott.aura.tapquery.service;

import com.marriott.aura.tapquery.document.UserEid;
import com.marriott.aura.tapquery.dto.ToolResponse;
import com.marriott.aura.tapquery.repository.UserEidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEidQueryService {

    private final UserEidRepository userRepo;

    public ToolResponse<List<UserEid>> getByEid(String eid) {
        log.debug("get_user_by_eid: {}", eid);
        List<UserEid> users = userRepo.findByEid(eid);
        Map<String, Object> q = Map.of("eid", eid);
        if (users.isEmpty()) return ToolResponse.notFound("get_user_by_eid", q);
        return ToolResponse.of("get_user_by_eid", users, users.size(), users.size(), 0, users.size(), q);
    }

    public ToolResponse<UserEid> getByEidAndApp(String eid, String app) {
        log.debug("get_user_by_eid_and_app: eid={}, app={}", eid, app);
        Optional<UserEid> user = userRepo.findByEidAndApp(eid, app);
        Map<String, Object> q = Map.of("eid", eid, "app", app);
        return user.map(u -> ToolResponse.single("get_user_by_eid_and_app", u, q))
                   .orElse(ToolResponse.notFound("get_user_by_eid_and_app", q));
    }

    public ToolResponse<List<UserEid>> findAll(String app, String status, int page, int size) {
        log.debug("find_users: app={}, status={}, page={}, size={}", app, status, page, size);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<UserEid> result;

        if (app != null && status != null) {
            result = userRepo.findByAppAndStatus(app, status, pr);
        } else if (app != null) {
            result = userRepo.findByApp(app, pr);
        } else if (status != null) {
            result = userRepo.findByStatus(status, pr);
        } else {
            result = userRepo.findAll(pr);
        }

        Map<String, Object> q = buildQuery("app", app, "status", status, "page", page, "size", size);
        return ToolResponse.of("find_users", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<UserEid>> findByLocation(String locationCode, int page, int size) {
        log.debug("find_users_by_location: locationCode={}", locationCode);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<UserEid> result = userRepo.findByLocationsContaining(locationCode, pr);
        Map<String, Object> q = Map.of("locationCode", locationCode, "page", page, "size", size);
        return ToolResponse.of("find_users_by_location", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<UserEid>> findByAssignment(String persona, int page, int size) {
        log.debug("find_users_by_assignment: persona={}", persona);
        PageRequest pr = PageRequest.of(page, size, Sort.by("eid").ascending());
        Page<UserEid> result = userRepo.findByAssignmentsContaining(persona, pr);
        Map<String, Object> q = Map.of("persona", persona, "page", page, "size", size);
        return ToolResponse.of("find_users_by_assignment", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<UserEid>> findByApp(String app, String status, int page, int size) {
        log.debug("find_users_by_app: app={}, status={}", app, status);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<UserEid> result = (status != null)
                ? userRepo.findByAppAndStatus(app, status, pr)
                : userRepo.findByApp(app, pr);
        Map<String, Object> q = buildQuery("app", app, "status", status, "page", page, "size", size);
        return ToolResponse.of("find_users_by_app", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<Map<String, Long>> count(String app, String status) {
        log.debug("count_users: app={}, status={}", app, status);
        long total;
        if (app != null && status != null) {
            total = userRepo.countByAppAndStatus(app, status);
        } else if (app != null) {
            total = userRepo.countByApp(app);
        } else if (status != null) {
            total = userRepo.countByStatus(status);
        } else {
            total = userRepo.count();
        }
        Map<String, Object> q = buildQuery("app", app, "status", status);
        return ToolResponse.single("count_users", Map.of("count", total), q);
    }

    private Map<String, Object> buildQuery(Object... kvPairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            if (kvPairs[i + 1] != null) result.put((String) kvPairs[i], kvPairs[i + 1]);
        }
        return result;
    }
}
