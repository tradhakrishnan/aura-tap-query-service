package com.marriott.aura.tapquery.service;

import com.marriott.aura.tapquery.document.ControlHotel;
import com.marriott.aura.tapquery.document.ControlLocation;
import com.marriott.aura.tapquery.dto.ToolResponse;
import com.marriott.aura.tapquery.repository.ControlHotelRepository;
import com.marriott.aura.tapquery.repository.ControlLocationRepository;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationQueryService {

    private final ControlLocationRepository locationRepo;
    private final ControlHotelRepository hotelRepo;

    public ToolResponse<ControlLocation> getById(String id) {
        log.debug("get_location_by_id: {}", id);
        Optional<ControlLocation> loc = locationRepo.findById(id);
        Map<String, Object> q = Map.of("id", id);
        return loc.map(l -> ToolResponse.single("get_location_by_id", l, q))
                  .orElse(ToolResponse.notFound("get_location_by_id", q));
    }

    public ToolResponse<List<ControlLocation>> findAll(String app, String status, int page, int size) {
        log.debug("find_locations: app={}, status={}, page={}, size={}", app, status, page, size);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlLocation> result;

        if (app != null && status != null) {
            result = locationRepo.findByAppAndStatus(app, status, pr);
        } else if (app != null) {
            result = locationRepo.findByApp(app, pr);
        } else if (status != null) {
            result = locationRepo.findByStatus(status, pr);
        } else {
            result = locationRepo.findAll(pr);
        }

        Map<String, Object> q = buildQuery("app", app, "status", status, "page", page, "size", size);
        return ToolResponse.of("find_locations", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<ControlLocation>> searchByName(String search, int page, int size) {
        log.debug("search_locations_by_name: q={}", search);
        PageRequest pr = PageRequest.of(page, size, Sort.by("locationName").ascending());
        Page<ControlLocation> result = locationRepo.findByLocationNameRegex("(?i)" + search, pr);
        Map<String, Object> q = Map.of("q", search, "page", page, "size", size);
        return ToolResponse.of("search_locations_by_name", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<ControlLocation>> findByHotel(String hotelCode, int page, int size) {
        log.debug("find_locations_by_hotel: hotelCode={}", hotelCode);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlLocation> result = locationRepo.findByControlledHotelsContaining(hotelCode, pr);
        Map<String, Object> q = Map.of("hotelCode", hotelCode, "page", page, "size", size);
        return ToolResponse.of("find_locations_by_hotel", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<ControlLocation>> findBySupervisor(String eid, int page, int size) {
        log.debug("find_locations_by_supervisor: eid={}", eid);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlLocation> result = locationRepo.findBySupervisorEidsContaining(eid, pr);
        Map<String, Object> q = Map.of("supervisorEid", eid, "page", page, "size", size);
        return ToolResponse.of("find_locations_by_supervisor", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<ControlLocation>> findByApp(String app, String status, int page, int size) {
        log.debug("find_locations_by_app: app={}, status={}", app, status);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlLocation> result = (status != null)
                ? locationRepo.findByAppAndStatus(app, status, pr)
                : locationRepo.findByApp(app, pr);
        Map<String, Object> q = buildQuery("app", app, "status", status, "page", page, "size", size);
        return ToolResponse.of("find_locations_by_app", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<Map<String, Long>> count(String app, String status) {
        log.debug("count_locations: app={}, status={}", app, status);
        long total;
        if (app != null && status != null) {
            total = locationRepo.countByAppAndStatus(app, status);
        } else if (app != null) {
            total = locationRepo.countByApp(app);
        } else if (status != null) {
            total = locationRepo.countByStatus(status);
        } else {
            total = locationRepo.count();
        }
        Map<String, Object> q = buildQuery("app", app, "status", status);
        return ToolResponse.single("count_locations", Map.of("count", total), q);
    }

    public ToolResponse<Map<String, Object>> getHotelSummary(String id) {
        log.debug("get_location_hotel_summary: id={}", id);
        Map<String, Object> q = Map.of("id", id);
        Optional<ControlLocation> loc = locationRepo.findById(id);
        if (loc.isEmpty()) return ToolResponse.notFound("get_location_hotel_summary", q);

        List<String> allCodes = loc.get().getControlledHotels();
        if (allCodes == null || allCodes.isEmpty()) {
            return ToolResponse.single("get_location_hotel_summary",
                    Map.of("totalInArray", 0, "active", 0, "inactive", 0,
                           "unknownCodes", List.of(), "inactiveCodes", List.of()), q);
        }

        List<ControlHotel> found = hotelRepo.findByIdIn(allCodes);
        Map<String, String> statusById = found.stream()
                .collect(Collectors.toMap(ControlHotel::getId, ControlHotel::getStatus));

        List<String> activeCodes   = allCodes.stream().filter(c -> "Active".equals(statusById.get(c))).toList();
        List<String> inactiveCodes = allCodes.stream().filter(c -> "Inactive".equals(statusById.get(c))).toList();
        List<String> unknownCodes  = allCodes.stream().filter(c -> !statusById.containsKey(c)).toList();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("locationId",    id);
        summary.put("totalInArray",  allCodes.size());
        summary.put("active",        activeCodes.size());
        summary.put("inactive",      inactiveCodes.size());
        summary.put("unknown",       unknownCodes.size());
        summary.put("inactiveCodes", inactiveCodes);
        summary.put("unknownCodes",  unknownCodes);

        return ToolResponse.single("get_location_hotel_summary", summary, q);
    }

    private Map<String, Object> buildQuery(Object... kvPairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            if (kvPairs[i + 1] != null) result.put((String) kvPairs[i], kvPairs[i + 1]);
        }
        return result;
    }
}
