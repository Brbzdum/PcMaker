package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.model.Manufacturer;
import ru.bek.compshp.service.ManufacturerService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manufacturers")
@RequiredArgsConstructor
@Tag(name = "Производители", description = "API для работы с производителями")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    @Operation(summary = "Получить всех производителей")
    public ResponseEntity<List<Manufacturer>> getAllManufacturers() {
        return ResponseEntity.ok(manufacturerService.getAllManufacturers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить производителя по ID")
    public ResponseEntity<Manufacturer> getManufacturerById(@PathVariable Long id) {
        return manufacturerService.getManufacturerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/name")
    @Operation(summary = "Получить имя производителя по ID")
    public ResponseEntity<Map<String, String>> getManufacturerNameById(@PathVariable Long id) {
        String name = manufacturerService.getManufacturerNameById(id);
        return ResponseEntity.ok(Map.of("name", name));
    }
} 