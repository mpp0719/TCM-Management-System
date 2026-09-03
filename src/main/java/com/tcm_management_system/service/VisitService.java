package com.tcm_management_system.service;

import com.tcm_management_system.dto.*;
import com.tcm_management_system.exception.MissingFieldException;
import com.tcm_management_system.exception.VisitAlreadyPaidException;
import com.tcm_management_system.exception.VisitNotFoundException;
import com.tcm_management_system.model.Patient;
import com.tcm_management_system.model.Visit;
import com.tcm_management_system.model.VisitMedication;
import com.tcm_management_system.repository.PaymentRepository;
import com.tcm_management_system.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PaymentRepository paymentRepository;

    public VisitService(VisitRepository visitRepository, PaymentRepository paymentRepository) {
        this.visitRepository = visitRepository;
        this.paymentRepository = paymentRepository;
    }

    public VisitResponseDto addVisit(Patient patient, VisitRequestDto dto) {
        if (dto.getVisitDate() == null) throw new MissingFieldException("visitDate");
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setVisitDate(dto.getVisitDate());
        visit.setComplaint(dto.getComplaint());
        visit.setTreatmentPlan(dto.getTreatmentPlan());
        visit.setNextAppointmentDate(dto.getNextAppointmentDate());
        return toDto(visitRepository.save(visit));
    }

    public List<VisitResponseDto> getHistoryForPatient(Long patientId) {
        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private VisitResponseDto toDto(Visit visit) {
        VisitResponseDto dto = new VisitResponseDto();
        dto.setId(visit.getId());
        dto.setPatientId(visit.getPatient().getId());
        dto.setVisitDate(visit.getVisitDate());
        dto.setComplaint(visit.getComplaint());
        dto.setTreatmentPlan(visit.getTreatmentPlan());
        dto.setNextAppointmentDate(visit.getNextAppointmentDate());
        dto.setPatientName(visit.getPatient().getName());
        dto.setPatientPhoneNum(visit.getPatient().getPhoneNum());
        dto.setHasPayment(paymentRepository.existsByVisitId(visit.getId()));
        dto.setMedications(visit.getMedications().stream()
                .map(this::toMedicationDto).collect(Collectors.toList()));
        return dto;
    }

    private DispensedMedicineDto toMedicationDto(VisitMedication vm) {
        DispensedMedicineDto dto = new DispensedMedicineDto();
        dto.setMedicineId(vm.getMedicine().getId());
        dto.setMedicineName(vm.getMedicine().getName());
        dto.setQuantityDispensed(vm.getQuantityDispensed());
        dto.setUnit(vm.getMedicine().getUnit());
        return dto;
    }

    public Map<LocalDate, List<VisitResponseDto>> getAppointmentsInRange(LocalDate start, LocalDate end) {
        return visitRepository.findByNextAppointmentDateBetween(start, end).stream()
                .map(this::toDto)
                .collect(Collectors.groupingBy(VisitResponseDto::getNextAppointmentDate));
    }

    public VisitResponseDto updateVisit(Long visitId, VisitRequestDto dto) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));
        if (paymentRepository.existsByVisitId(visitId)) {
            throw new VisitAlreadyPaidException(visitId);
        }
        if (dto.getVisitDate() == null) throw new MissingFieldException("visitDate");
        visit.setVisitDate(dto.getVisitDate());
        visit.setComplaint(dto.getComplaint());
        visit.setTreatmentPlan(dto.getTreatmentPlan());
        visit.setNextAppointmentDate(dto.getNextAppointmentDate());
        return toDto(visitRepository.save(visit));
    }
}