package com.tcm_management_system.controller;

import com.tcm_management_system.dto.PaymentRequestDto;
import com.tcm_management_system.dto.PaymentResponseDto;
import com.tcm_management_system.dto.PaymentUpdateDto;
import com.tcm_management_system.service.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponseDto create(@RequestBody PaymentRequestDto dto) {
        return paymentService.create(dto);
    }

    @GetMapping("/visit/{visitId}")
    public PaymentResponseDto getByVisit(@PathVariable Long visitId) {
        return paymentService.getByVisitId(visitId);
    }

    @PutMapping("/{id}")
    public PaymentResponseDto update(@PathVariable Long id, @RequestBody PaymentUpdateDto dto) {
        return paymentService.update(id, dto);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        byte[] excelBytes = paymentService.exportTransactionsToExcel(start, end);
        String filename = "transactions-" + start + "-to-" + end + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}