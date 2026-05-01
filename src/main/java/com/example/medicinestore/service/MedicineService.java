package com.example.medicinestore.service;

import com.example.medicinestore.entity.Medicine;
import com.example.medicinestore.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public Medicine addMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Transactional
    public Medicine updateMedicine(Long id, Medicine updatedMedicine) {
        Medicine existing = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with id: " + id));
        existing.setName(updatedMedicine.getName());
        existing.setPrice(updatedMedicine.getPrice());
        existing.setQuantity(updatedMedicine.getQuantity());
        return existing;
    }

    @Transactional
    public void sellMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with id: " + id));
        if (medicine.getQuantity() <= 0) {
            throw new IllegalStateException("Cannot sell medicine. Stock is empty.");
        }
        medicine.setQuantity(medicine.getQuantity() - 1);
    }
}
