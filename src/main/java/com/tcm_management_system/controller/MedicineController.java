package com.tcm_management_system.controller;

import com.tcm_management_system.dto.*;
import com.tcm_management_system.model.MedicineRestock;
import com.tcm_management_system.service.MedicineService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public List<MedicineResponseDto> getAll() {
        return medicineService.getAll();
    }

    @PostMapping
    public MedicineResponseDto create(@RequestBody MedicineRequestDto dto) {
        return medicineService.create(dto);
    }

    @GetMapping("/{id}/stock")
    public int getStock(@PathVariable Long id) {
        return medicineService.getCurrentStock(id);
    }

    @PostMapping("/dispense")
    public DispensedMedicineDto dispense(@RequestBody DispenseRequestDto dto) {
        return medicineService.dispense(dto);
    }

    @PostMapping("/restock")
    public RestockResponseDto restock(@RequestBody RestockRequestDto dto) {
        return medicineService.restock(dto);
    }

    @GetMapping("/{id}/restock-history")
    public List<RestockResponseDto> getRestockHistory(@PathVariable Long id) {
        return medicineService.getRestockHistory(id);
    }

    @PutMapping("/{id}")
    public MedicineResponseDto update(@PathVariable Long id, @RequestBody MedicineUpdateDto dto) {
        return medicineService.update(id, dto);
    }
}