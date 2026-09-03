package com.tcm_management_system.repository;
import com.tcm_management_system.model.MedicineRestock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicineRestockRepository extends JpaRepository<MedicineRestock, Long> {
    List<MedicineRestock> findByMedicineIdOrderByRestockDateDesc(Long medicineId);
}