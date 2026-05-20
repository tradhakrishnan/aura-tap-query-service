package com.marriott.aura.tapquery.service;

import com.marriott.aura.tapquery.document.ControlHotel;
import com.marriott.aura.tapquery.dto.ToolResponse;
import com.marriott.aura.tapquery.repository.ControlHotelRepository;
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
public class HotelQueryService {

    private final ControlHotelRepository hotelRepo;

    public ToolResponse<ControlHotel> getById(String id) {
        log.debug("get_hotel_by_id: {}", id);
        Optional<ControlHotel> hotel = hotelRepo.findById(id);
        Map<String, Object> q = Map.of("id", id);
        return hotel.map(h -> ToolResponse.single("get_hotel_by_id", h, q))
                    .orElse(ToolResponse.notFound("get_hotel_by_id", q));
    }

    public ToolResponse<List<ControlHotel>> findAll(String status, String crsSystem, int page, int size) {
        log.debug("find_hotels: status={}, crsSystem={}, page={}, size={}", status, crsSystem, page, size);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlHotel> result;

        if (status != null && crsSystem != null) {
            result = hotelRepo.findByCrsSystemAndStatus(crsSystem, status, pr);
        } else if (status != null) {
            result = hotelRepo.findByStatus(status, pr);
        } else if (crsSystem != null) {
            result = hotelRepo.findByCrsSystem(crsSystem, pr);
        } else {
            result = hotelRepo.findAll(pr);
        }

        Map<String, Object> q = buildQuery("status", status, "crsSystem", crsSystem, "page", page, "size", size);
        return ToolResponse.of("find_hotels", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<ControlHotel>> searchByName(String q, int page, int size) {
        log.debug("search_hotels_by_name: q={}", q);
        PageRequest pr = PageRequest.of(page, size, Sort.by("locationName").ascending());
        String regex = "(?i)" + q;
        Page<ControlHotel> result = hotelRepo.findByLocationNameRegex(regex, pr);
        Map<String, Object> query = Map.of("q", q, "page", page, "size", size);
        return ToolResponse.of("search_hotels_by_name", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, query);
    }

    public ToolResponse<List<ControlHotel>> getByIds(List<String> ids) {
        log.debug("get_hotels_by_ids: count={}", ids.size());
        List<ControlHotel> hotels = hotelRepo.findByIdIn(ids);
        Map<String, Object> q = Map.of("ids", ids, "requested", ids.size());
        return ToolResponse.of("get_hotels_by_ids", hotels, hotels.size(), hotels.size(), 0, ids.size(), q);
    }

    public ToolResponse<List<ControlHotel>> findByCrs(String crsSystem, int page, int size) {
        log.debug("find_hotels_by_crs: crsSystem={}", crsSystem);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlHotel> result = hotelRepo.findByCrsSystem(crsSystem, pr);
        Map<String, Object> q = Map.of("crsSystem", crsSystem, "page", page, "size", size);
        return ToolResponse.of("find_hotels_by_crs", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<List<ControlHotel>> findByStatus(String status, int page, int size) {
        log.debug("find_hotels_by_status: status={}", status);
        PageRequest pr = PageRequest.of(page, size, Sort.by("updatedOn").descending());
        Page<ControlHotel> result = hotelRepo.findByStatus(status, pr);
        Map<String, Object> q = Map.of("status", status, "page", page, "size", size);
        return ToolResponse.of("find_hotels_by_status", result.getContent(),
                result.getNumberOfElements(), result.getTotalElements(), page, size, q);
    }

    public ToolResponse<Map<String, Long>> count(String status, String crsSystem) {
        log.debug("count_hotels: status={}, crsSystem={}", status, crsSystem);
        long total;
        if (status != null && crsSystem != null) {
            total = hotelRepo.countByCrsSystemAndStatus(crsSystem, status);
        } else if (status != null) {
            total = hotelRepo.countByStatus(status);
        } else if (crsSystem != null) {
            total = hotelRepo.countByCrsSystem(crsSystem);
        } else {
            total = hotelRepo.count();
        }
        Map<String, Object> q = buildQuery("status", status, "crsSystem", crsSystem);
        return ToolResponse.single("count_hotels", Map.of("count", total), q);
    }

    private Map<String, Object> buildQuery(Object... kvPairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            if (kvPairs[i + 1] != null) result.put((String) kvPairs[i], kvPairs[i + 1]);
        }
        return result;
    }
}
