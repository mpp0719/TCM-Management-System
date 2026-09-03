package com.tcm_management_system.service;

import com.tcm_management_system.dto.PatientRequestDto;
import com.tcm_management_system.dto.PatientResponseDto;
import com.tcm_management_system.exception.DuplicateIcException;
import com.tcm_management_system.exception.MissingFieldException;
import com.tcm_management_system.exception.PatientNotFoundException;
import com.tcm_management_system.model.Patient;
import com.tcm_management_system.repository.PatientRepository;
import com.tcm_management_system.repository.VisitRepository;
import com.tcm_management_system.util.IcNoUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public PatientService(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    public List<PatientResponseDto> search(String term) {
        String safeTerm = term == null ? "" : term;
        String icTerm = IcNoUtil.normalize(safeTerm);
        List<Patient> patients = patientRepository.search(safeTerm, icTerm);

        Map<Long, LocalDate> lastVisitMap = new HashMap<>();
        for (Object[] row : visitRepository.findLastVisitDatesByPatient()) {
            lastVisitMap.put((Long) row[0], (LocalDate) row[1]);
        }
        Map<Long, LocalDate> upcomingApptMap = new HashMap<>();
        for (Object[] row : visitRepository.findNextUpcomingAppointmentsByPatient()) {
            upcomingApptMap.put((Long) row[0], (LocalDate) row[1]);
        }

        List<PatientResponseDto> dtos = patients.stream()
                .map(p -> toDto(p, lastVisitMap.get(p.getId()), upcomingApptMap.get(p.getId())))
                .collect(Collectors.toList());

        dtos.sort(Comparator.comparingLong(this::relevanceKey));
        return dtos;
    }

    public PatientResponseDto getDtoById(Long id) {
        return toDto(getById(id));
    }

    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    public Optional<Patient> findByIcNo(String icNo) {
        return patientRepository.findByIcNo(IcNoUtil.normalize(icNo));
    }

    public PatientResponseDto create(PatientRequestDto dto) {
        validateRequired(dto);
        String normalizedIc = IcNoUtil.normalizeAndValidate(dto.getIcNo());
        if (patientRepository.findByIcNo(normalizedIc).isPresent()) {
            throw new DuplicateIcException(normalizedIc);
        }
        Patient patient = new Patient();
        applyDto(patient, dto, normalizedIc);
        return toDto(patientRepository.save(patient));
    }

    public PatientResponseDto update(Long id, PatientRequestDto dto) {
        validateRequired(dto);
        String normalizedIc = IcNoUtil.normalizeAndValidate(dto.getIcNo());
        Patient patient = getById(id);
        applyDto(patient, dto, normalizedIc);
        return toDto(patientRepository.save(patient));
    }

    // helper fn below

    private void applyDto(Patient patient, PatientRequestDto dto, String normalizedIc) {
        patient.setName(dto.getName());
        patient.setIcNo(normalizedIc);
        patient.setGender(dto.getGender());
        patient.setAddress(dto.getAddress());
        patient.setPhoneNum(dto.getPhoneNum());
    }

    private PatientResponseDto toDto(Patient patient) {
        LocalDate lastVisit = visitRepository.findLastVisitDate(patient.getId());
        LocalDate nextAppt = visitRepository.findNextUpcomingAppointment(patient.getId());
        return toDto(patient, lastVisit, nextAppt);
    }

    private PatientResponseDto toDto(Patient patient, LocalDate lastVisitDate, LocalDate nextAppointmentDate) {
        PatientResponseDto dto = new PatientResponseDto();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setIcNo(IcNoUtil.formatWithDashes(patient.getIcNo()));
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setPhoneNum(patient.getPhoneNum());
        dto.setLastVisitDate(lastVisitDate);
        dto.setNextAppointmentDate(nextAppointmentDate);
        return dto;
    }

    private long relevanceKey(PatientResponseDto dto) {
        if (dto.getNextAppointmentDate() != null) {
            return dto.getNextAppointmentDate().toEpochDay(); // soonest upcoming appt first
        }
        if (dto.getLastVisitDate() != null) {
            return 1_000_000L - dto.getLastVisitDate().toEpochDay(); // most recent past visit first
        }
        return Long.MAX_VALUE; // never visited, sorts last
    }

    private void validateRequired(PatientRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new MissingFieldException("name");
        if (dto.getIcNo() == null || dto.getIcNo().isBlank()) throw new MissingFieldException("icNo");
    }
}