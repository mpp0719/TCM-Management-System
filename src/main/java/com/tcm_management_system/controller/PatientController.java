package com.tcm_management_system.controller;

import com.tcm_management_system.dto.PatientRequestDto;
import com.tcm_management_system.dto.PatientResponseDto;
import com.tcm_management_system.service.PatientService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/search")
    public List<PatientResponseDto> search(@RequestParam(required = false) String term) {
        return patientService.search(term);
    }

    @GetMapping("/{id}")
    public PatientResponseDto getById(@PathVariable Long id) {
        return patientService.getDtoById(id);
    }

    @PostMapping
    public PatientResponseDto create(@RequestBody PatientRequestDto dto) {
        return patientService.create(dto);
    }

    @PutMapping("/{id}")
    public PatientResponseDto update(@PathVariable Long id, @RequestBody PatientRequestDto dto) {
        return patientService.update(id, dto);
    }
}