package com.example.medicinestore.controller;

import com.example.medicinestore.entity.Medicine;
import com.example.medicinestore.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/medicines";
    }

    @GetMapping("/medicines")
    public String listMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "medicines";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        return "add";
    }

    @PostMapping("/save")
    public String saveMedicine(@Valid @ModelAttribute("medicine") Medicine medicine,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "add";
        }
        medicineService.addMedicine(medicine);
        redirectAttributes.addFlashAttribute("successMessage", "Medicine added successfully.");
        return "redirect:/manage";
    }

    @GetMapping("/manage")
    public String manageMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "manage";
    }

    @GetMapping("/sell/{id}")
    public String sellMedicine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicineService.sellMedicine(id);
            redirectAttributes.addFlashAttribute("successMessage", "Medicine sold successfully.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/manage";
    }
}
