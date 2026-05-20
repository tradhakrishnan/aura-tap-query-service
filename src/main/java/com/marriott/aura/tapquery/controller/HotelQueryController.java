package com.marriott.aura.tapquery.controller;

import com.marriott.aura.tapquery.document.ControlHotel;
import com.marriott.aura.tapquery.dto.ToolResponse;
import com.marriott.aura.tapquery.service.HotelQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelQueryController {

    private final HotelQueryService service;

    // GET /api/hotels/{id}
    @GetMapping("/{id}")
    public ToolResponse<ControlHotel> getById(@PathVariable String id) {
        return service.getById(id);
    }

    // GET /api/hotels?status=Active&crsSystem=MARSHA&page=0&size=20
    @GetMapping
    public ToolResponse<List<ControlHotel>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String crsSystem,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findAll(status, crsSystem, page, size);
    }

    // GET /api/hotels/search?q=Marriott+Atlanta&page=0&size=20
    @GetMapping("/search")
    public ToolResponse<List<ControlHotel>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.searchByName(q, page, size);
    }

    // POST /api/hotels/batch   body: ["PARBA","NYCES","CHIGS"]
    @PostMapping("/batch")
    public ToolResponse<List<ControlHotel>> getByIds(@RequestBody List<String> ids) {
        return service.getByIds(ids);
    }

    // GET /api/hotels/by-crs/MARSHA?page=0&size=20
    @GetMapping("/by-crs/{crsSystem}")
    public ToolResponse<List<ControlHotel>> byCrs(
            @PathVariable String crsSystem,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByCrs(crsSystem, page, size);
    }

    // GET /api/hotels/by-status/Active?page=0&size=20
    @GetMapping("/by-status/{status}")
    public ToolResponse<List<ControlHotel>> byStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByStatus(status, page, size);
    }

    // GET /api/hotels/count?status=Active&crsSystem=MARSHA
    @GetMapping("/count")
    public ToolResponse<Map<String, Long>> count(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String crsSystem) {
        return service.count(status, crsSystem);
    }
}
