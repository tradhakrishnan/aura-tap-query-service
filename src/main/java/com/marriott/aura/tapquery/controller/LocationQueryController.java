package com.marriott.aura.tapquery.controller;

import com.marriott.aura.tapquery.document.ControlLocation;
import com.marriott.aura.tapquery.dto.ToolResponse;
import com.marriott.aura.tapquery.service.LocationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocationQueryController {

    private final LocationQueryService service;

    // GET /api/locations/{id}
    @GetMapping("/{id}")
    public ToolResponse<ControlLocation> getById(@PathVariable String id) {
        return service.getById(id);
    }

    // GET /api/locations?app=MARSHA&status=Active&page=0&size=20
    @GetMapping
    public ToolResponse<List<ControlLocation>> findAll(
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findAll(app, status, page, size);
    }

    // GET /api/locations/search?q=Revenue+Management&page=0&size=20
    @GetMapping("/search")
    public ToolResponse<List<ControlLocation>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.searchByName(q, page, size);
    }

    // GET /api/locations/by-hotel/PARBA?page=0&size=20
    @GetMapping("/by-hotel/{hotelCode}")
    public ToolResponse<List<ControlLocation>> byHotel(
            @PathVariable String hotelCode,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByHotel(hotelCode, page, size);
    }

    // GET /api/locations/by-supervisor/NMKOS046?page=0&size=20
    @GetMapping("/by-supervisor/{eid}")
    public ToolResponse<List<ControlLocation>> bySupervisor(
            @PathVariable String eid,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findBySupervisor(eid, page, size);
    }

    // GET /api/locations/by-app/MARSHA?status=Active&page=0&size=20
    @GetMapping("/by-app/{app}")
    public ToolResponse<List<ControlLocation>> byApp(
            @PathVariable String app,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByApp(app, status, page, size);
    }

    // GET /api/locations/count?app=MARSHA&status=Active
    @GetMapping("/count")
    public ToolResponse<Map<String, Long>> count(
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String status) {
        return service.count(app, status);
    }

    // GET /api/locations/{id}/hotel-summary  — active/inactive/unknown breakdown for controlledHotels
    @GetMapping("/{id}/hotel-summary")
    public ToolResponse<Map<String, Object>> hotelSummary(@PathVariable String id) {
        return service.getHotelSummary(id);
    }
}
