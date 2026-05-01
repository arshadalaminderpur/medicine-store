package com.example.medicinestore.controller;

import jakarta.validation.constraints.Min;

public class SellMedicineRequest {

    @Min(value = 1, message = "Quantity to sell must be greater than 0")
    private int quantityToSell;

    public int getQuantityToSell() {
        return quantityToSell;
    }

    public void setQuantityToSell(int quantityToSell) {
        this.quantityToSell = quantityToSell;
    }
}
