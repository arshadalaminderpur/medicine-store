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
        normalizeMedicineName(medicine);
        validateUniqueNameForCreate(medicine.getName());
        return medicineRepository.save(medicine);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with id: " + id));
    }

    @Transactional
    public Medicine updateMedicine(Long id, Medicine updatedMedicine) {
        Medicine existing = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with id: " + id));
        normalizeMedicineName(updatedMedicine);
        validateUniqueNameForUpdate(updatedMedicine.getName(), id);
        existing.setName(updatedMedicine.getName());
        existing.setPrice(updatedMedicine.getPrice());
        existing.setQuantity(updatedMedicine.getQuantity());
        return existing;
    }

    @Transactional
    public double sellMedicine(Long id, int quantityToSell) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with id: " + id));
        if (quantityToSell <= 0) {
            throw new IllegalArgumentException("Quantity to sell must be greater than 0.");
        }
        if (quantityToSell > medicine.getQuantity()) {
            throw new IllegalStateException("Requested quantity exceeds available stock.");
        }
        medicine.setQuantity(medicine.getQuantity() - quantityToSell);
        return quantityToSell * medicine.getPrice();
    }

    private void normalizeMedicineName(Medicine medicine) {
        if (medicine.getName() != null) {
            medicine.setName(medicine.getName().trim());
        }
    }

    private void validateUniqueNameForCreate(String name) {
        if (medicineRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Medicine already exists");
        }
    }

    private void validateUniqueNameForUpdate(String name, Long id) {
        if (medicineRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalArgumentException("Medicine already exists");
        }
    }
}
