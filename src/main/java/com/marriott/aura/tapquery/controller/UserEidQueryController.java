package com.marriott.aura.tapquery.controller;

import com.marriott.aura.tapquery.document.UserEid;
import com.marriott.aura.tapquery.dto.ToolResponse;
import com.marriott.aura.tapquery.service.UserEidQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserEidQueryController {

    private final UserEidQueryService service;

    // GET /api/users/{eid}  → returns all app records for this EID
    @GetMapping("/{eid}")
    public ToolResponse<List<UserEid>> getByEid(@PathVariable String eid) {
        return service.getByEid(eid);
    }

    // GET /api/users/{eid}/{app}  → returns one specific app record
    @GetMapping("/{eid}/{app}")
    public ToolResponse<UserEid> getByEidAndApp(
            @PathVariable String eid,
            @PathVariable String app) {
        return service.getByEidAndApp(eid, app);
    }

    // GET /api/users?app=MARSHA&status=Active&page=0&size=20
    @GetMapping
    public ToolResponse<List<UserEid>> findAll(
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findAll(app, status, page, size);
    }

    // GET /api/users/by-location/HTDV7N?page=0&size=20
    @GetMapping("/by-location/{locationCode}")
    public ToolResponse<List<UserEid>> byLocation(
            @PathVariable String locationCode,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByLocation(locationCode, page, size);
    }

    // GET /api/users/by-assignment?persona=Revenue+Manager&page=0&size=20
    @GetMapping("/by-assignment")
    public ToolResponse<List<UserEid>> byAssignment(
            @RequestParam String persona,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByAssignment(persona, page, size);
    }

    // GET /api/users/by-app/MARSHA?status=Active&page=0&size=20
    @GetMapping("/by-app/{app}")
    public ToolResponse<List<UserEid>> byApp(
            @PathVariable String app,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findByApp(app, status, page, size);
    }

    // GET /api/users/count?app=MARSHA&status=Active
    @GetMapping("/count")
    public ToolResponse<Map<String, Long>> count(
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String status) {
        return service.count(app, status);
    }
}
