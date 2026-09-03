package com.tcm_management_system.service;

import com.tcm_management_system.dto.PaymentRequestDto;
import com.tcm_management_system.dto.PaymentUpdateDto;
import com.tcm_management_system.dto.PaymentResponseDto;
import com.tcm_management_system.exception.*;
import com.tcm_management_system.model.Payment;
import com.tcm_management_system.model.Visit;
import com.tcm_management_system.model.VisitMedication;
import com.tcm_management_system.repository.PaymentRepository;
import com.tcm_management_system.repository.VisitMedicationRepository;
import com.tcm_management_system.repository.VisitRepository;
import com.tcm_management_system.util.IcNoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;

@Service
public class PaymentService {

    private static final Set<String> ALLOWED_METHODS =
            Set.of("Cash", "Card", "Bank Transfer", "E-Wallet");

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final VisitMedicationRepository visitMedicationRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          VisitRepository visitRepository,
                          VisitMedicationRepository visitMedicationRepository) {
        this.paymentRepository = paymentRepository;
        this.visitRepository = visitRepository;
        this.visitMedicationRepository = visitMedicationRepository;
    }

    @Transactional
    public PaymentResponseDto create(PaymentRequestDto dto) {
        if (dto.getVisitId() == null) throw new MissingFieldException("visitId");
        Visit visit = visitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new VisitNotFoundException(dto.getVisitId()));

        if (paymentRepository.existsByVisitId(visit.getId())) {
            throw new PaymentAlreadyExistsException(visit.getId());
        }

        if (dto.getPaymentDate() == null) throw new MissingFieldException("paymentDate");

        if (dto.getPaymentMethod() == null || dto.getPaymentMethod().isBlank()) {
            throw new MissingFieldException("paymentMethod");
        }
        if (!ALLOWED_METHODS.contains(dto.getPaymentMethod())) {
            throw new InvalidPaymentMethodException(dto.getPaymentMethod());
        }

        if (dto.getTreatmentFee() == null) throw new MissingFieldException("treatmentFee");
        if (dto.getTreatmentFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("treatmentFee", dto.getTreatmentFee());
        }

        BigDecimal medicineCost = computeMedicineCost(visit.getId());
        BigDecimal totalAmount = dto.getTreatmentFee().add(medicineCost);

        Payment payment = new Payment();
        payment.setVisit(visit);
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTreatmentFee(dto.getTreatmentFee());
        payment.setMedicineCost(medicineCost);
        payment.setTotalAmount(totalAmount);

        return toDto(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDto update(Long paymentId, PaymentUpdateDto dto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> PaymentNotFoundException.forPaymentId(paymentId));

        if (dto.getPaymentDate() == null) throw new MissingFieldException("paymentDate");
        if (dto.getPaymentMethod() == null || dto.getPaymentMethod().isBlank()) {
            throw new MissingFieldException("paymentMethod");
        }
        if (!ALLOWED_METHODS.contains(dto.getPaymentMethod())) {
            throw new InvalidPaymentMethodException(dto.getPaymentMethod());
        }
        if (dto.getTreatmentFee() == null) throw new MissingFieldException("treatmentFee");
        if (dto.getTreatmentFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("treatmentFee", dto.getTreatmentFee());
        }

        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTreatmentFee(dto.getTreatmentFee());
        // medicineCost is deliberately left untouched — dispensing is now locked
        // once a visit is paid (see MedicineService.dispense below), so it can
        // never drift out of sync with what was actually given.
        payment.setTotalAmount(dto.getTreatmentFee().add(payment.getMedicineCost()));

        return toDto(paymentRepository.save(payment));
    }

    public PaymentResponseDto getByVisitId(Long visitId) {
        Payment payment = paymentRepository.findByVisitId(visitId)
                .orElseThrow(() -> PaymentNotFoundException.forVisit(visitId));
        return toDto(payment);
    }

    private BigDecimal computeMedicineCost(Long visitId) {
        List<VisitMedication> dispensed = visitMedicationRepository.findByVisitId(visitId);
        BigDecimal total = BigDecimal.ZERO;
        for (VisitMedication vm : dispensed) {
            BigDecimal lineTotal = vm.getMedicine().getSellingPrice()
                    .multiply(BigDecimal.valueOf(vm.getQuantityDispensed()));
            total = total.add(lineTotal);
        }
        return total;
    }

    private PaymentResponseDto toDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setVisitId(payment.getVisit().getId());
        dto.setPatientName(payment.getVisit().getPatient().getName());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTreatmentFee(payment.getTreatmentFee());
        dto.setMedicineCost(payment.getMedicineCost());
        dto.setTotalAmount(payment.getTotalAmount());
        return dto;
    }

    public byte[] exportTransactionsToExcel(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new InvalidDateRangeException(start, end);
        }
        List<Payment> payments = paymentRepository.findByPaymentDateBetweenOrderByPaymentDateAsc(start, end);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {"Payment Date", "Patient Name", "Patient IC", "Visit Date", "Payment Method", "Treatment Fee", "Medicine Cost", "Total Amount"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Payment p : payments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getPaymentDate().toString());
                row.createCell(1).setCellValue(p.getVisit().getPatient().getName());
                row.createCell(2).setCellValue(IcNoUtil.formatWithDashes(p.getVisit().getPatient().getIcNo()));
                row.createCell(3).setCellValue(p.getVisit().getVisitDate().toString());
                row.createCell(4).setCellValue(p.getPaymentMethod());
                row.createCell(5).setCellValue(p.getTreatmentFee().doubleValue());
                row.createCell(6).setCellValue(p.getMedicineCost().doubleValue());
                row.createCell(7).setCellValue(p.getTotalAmount().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel export: " + e.getMessage(), e);
        }
    }
}