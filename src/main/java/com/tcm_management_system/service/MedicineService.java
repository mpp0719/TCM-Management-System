package com.tcm_management_system.service;

import com.tcm_management_system.dto.*;
import com.tcm_management_system.exception.*;
import com.tcm_management_system.model.*;
import com.tcm_management_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineRestockRepository restockRepository;
    private final VisitMedicationRepository visitMedicationRepository;
    private final VisitRepository visitRepository;
    private final PaymentRepository paymentRepository;

    public MedicineService(MedicineRepository medicineRepository,
                           MedicineRestockRepository restockRepository,
                           VisitMedicationRepository visitMedicationRepository,
                           VisitRepository visitRepository, PaymentRepository paymentRepository) {
        this.medicineRepository = medicineRepository;
        this.restockRepository = restockRepository;
        this.visitMedicationRepository = visitMedicationRepository;
        this.visitRepository = visitRepository;
        this.paymentRepository = paymentRepository;
    }

    public MedicineResponseDto create(MedicineRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new MissingFieldException("name");
        if (dto.getUnit() == null || dto.getUnit().isBlank()) throw new MissingFieldException("unit");
        if (dto.getSellingPrice() == null || dto.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("sellingPrice", dto.getSellingPrice());
        }
        if (medicineRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateMedicineNameException(dto.getName());
        }
        Medicine medicine = new Medicine();
        medicine.setName(dto.getName());
        medicine.setUnit(dto.getUnit());
        medicine.setSellingPrice(dto.getSellingPrice());
        return toDto(medicineRepository.save(medicine));
    }

    public MedicineResponseDto update(Long id, MedicineUpdateDto dto) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException(id));
        if (dto.getName() == null || dto.getName().isBlank()) throw new MissingFieldException("name");
        if (dto.getSellingPrice() == null || dto.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("sellingPrice", dto.getSellingPrice());
        }
        medicine.setName(dto.getName());
        medicine.setSellingPrice(dto.getSellingPrice());
        return toDto(medicineRepository.save(medicine));
    }

    public int getCurrentStock(Long medicineId) {
        Integer stock = medicineRepository.getCurrentStock(medicineId);
        return stock != null ? stock : 0;
    }

    @Transactional
    public RestockResponseDto restock(RestockRequestDto dto) {
        if (dto.getRestockQty() == null || dto.getRestockQty() <= 0) {
            throw new InvalidQuantityException("restockQty", dto.getRestockQty() == null ? 0 : dto.getRestockQty());
        }
        if (dto.getUnitPrice() == null || dto.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("unitPrice", dto.getUnitPrice());
        }
        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException(dto.getMedicineId()));
        MedicineRestock restock = new MedicineRestock();
        restock.setMedicine(medicine);
        restock.setRestockQty(dto.getRestockQty());
        restock.setRestockDate(dto.getRestockDate());
        restock.setUnitPrice(dto.getUnitPrice());
        MedicineRestock saved = restockRepository.save(restock);
        RestockResponseDto response = new RestockResponseDto();
        response.setId(saved.getId());
        response.setMedicineId(medicine.getId());
        response.setMedicineName(medicine.getName());
        response.setRestockQty(saved.getRestockQty());
        response.setRestockDate(saved.getRestockDate());
        response.setUnitPrice(saved.getUnitPrice());
        return response;
    }

    @Transactional
    public DispensedMedicineDto dispense(DispenseRequestDto dto) {
        if (dto.getQuantityDispensed() == null || dto.getQuantityDispensed() <= 0) {
            throw new InvalidQuantityException("quantityDispensed", dto.getQuantityDispensed() == null ? 0 : dto.getQuantityDispensed());
        }
        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException(dto.getMedicineId()));
        Visit visit = visitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new VisitNotFoundException(dto.getVisitId()));
        if (paymentRepository.existsByVisitId(visit.getId())) {
            throw new VisitAlreadyPaidException(visit.getId());
        }

        int currentStock = getCurrentStock(medicine.getId());
        if (dto.getQuantityDispensed() > currentStock) {
            throw new InsufficientStockException(currentStock, dto.getQuantityDispensed());
        }

        VisitMedication vm = new VisitMedication();
        vm.setVisit(visit);
        vm.setMedicine(medicine);
        vm.setQuantityDispensed(dto.getQuantityDispensed());
        visitMedicationRepository.save(vm);

        DispensedMedicineDto result = new DispensedMedicineDto();
        result.setMedicineId(medicine.getId());
        result.setMedicineName(medicine.getName());
        result.setQuantityDispensed(dto.getQuantityDispensed());
        result.setUnit(medicine.getUnit());
        return result;
    }

    public List<MedicineResponseDto> getAll() {
        return medicineRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RestockResponseDto> getRestockHistory(Long medicineId) {
        return restockRepository.findByMedicineIdOrderByRestockDateDesc(medicineId)
                .stream().map(this::toRestockDto).collect(Collectors.toList());
    }

    private MedicineResponseDto toDto(Medicine medicine) {
        MedicineResponseDto dto = new MedicineResponseDto();
        dto.setId(medicine.getId());
        dto.setName(medicine.getName());
        dto.setUnit(medicine.getUnit());
        dto.setSellingPrice(medicine.getSellingPrice());
        dto.setCurrentStock(getCurrentStock(medicine.getId()));
        return dto;
    }

    private RestockResponseDto toRestockDto(MedicineRestock restock) {
        RestockResponseDto dto = new RestockResponseDto();
        dto.setId(restock.getId());
        dto.setMedicineId(restock.getMedicine().getId());
        dto.setMedicineName(restock.getMedicine().getName());
        dto.setRestockQty(restock.getRestockQty());
        dto.setRestockDate(restock.getRestockDate());
        dto.setUnitPrice(restock.getUnitPrice());
        return dto;
    }
}