package com.example.medicinestore.controller;

import com.example.medicinestore.entity.Medicine;
import com.example.medicinestore.service.MedicineService;
import jakarta.persistence.EntityNotFoundException;
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
        try {
            medicineService.addMedicine(medicine);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            return "add";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Medicine added successfully.");
        return "redirect:/manage";
    }

    @GetMapping("/manage")
    public String manageMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "manage";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("medicine", medicineService.getMedicineById(id));
            return "edit";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/manage";
        }
    }

    @PostMapping("/update/{id}")
    public String updateMedicine(@PathVariable Long id,
                                 @Valid @ModelAttribute("medicine") Medicine medicine,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        try {
            medicineService.updateMedicine(id, medicine);
            redirectAttributes.addFlashAttribute("successMessage", "Medicine updated successfully.");
            return "redirect:/manage";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            return "edit";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/manage";
        }
    }

    @GetMapping("/sell/{id}")
    public String showSellForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("medicine", medicineService.getMedicineById(id));
            model.addAttribute("sellRequest", new SellMedicineRequest());
            return "sell";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/manage";
        }
    }

    @PostMapping("/sell/{id}")
    public String sellMedicine(@PathVariable Long id,
                               @Valid @ModelAttribute("sellRequest") SellMedicineRequest sellRequest,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Medicine medicine;
        try {
            medicine = medicineService.getMedicineById(id);
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/manage";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("medicine", medicine);
            return "sell";
        }

        try {
            double totalPrice = medicineService.sellMedicine(id, sellRequest.getQuantityToSell());
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    String.format("Sold %d unit(s) of %s. Total price: %.2f",
                            sellRequest.getQuantityToSell(),
                            medicine.getName(),
                            totalPrice)
            );
            return "redirect:/manage";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("medicine", medicine);
            model.addAttribute("errorMessage", ex.getMessage());
            return "sell";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/manage";
    }
}
