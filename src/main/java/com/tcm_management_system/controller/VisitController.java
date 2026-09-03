package com.tcm_management_system.controller;

import com.tcm_management_system.dto.VisitRequestDto;
import com.tcm_management_system.dto.VisitResponseDto;
import com.tcm_management_system.model.Patient;
import com.tcm_management_system.service.PatientService;
import com.tcm_management_system.service.VisitService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final PatientService patientService;

    public VisitController(VisitService visitService, PatientService patientService) {
        this.visitService = visitService;
        this.patientService = patientService;
    }

    @GetMapping("/patient/{patientId}")
    public List<VisitResponseDto> getHistory(@PathVariable Long patientId) {
        return visitService.getHistoryForPatient(patientId);
    }

    @PostMapping("/patient/{patientId}")
    public VisitResponseDto addVisit(@PathVariable Long patientId, @RequestBody VisitRequestDto dto) {
        Patient patient = patientService.getById(patientId); // throws if not found -> needs handling, see below
        return visitService.addVisit(patient, dto);
    }

    @GetMapping("/appointments")
    public Map<LocalDate, List<VisitResponseDto>> getAppointments(
            @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return visitService.getAppointmentsInRange(start, end);
    }

    @PutMapping("/{id}")
    public VisitResponseDto update(@PathVariable Long id, @RequestBody VisitRequestDto dto) {
        return visitService.updateVisit(id, dto);
    }
}