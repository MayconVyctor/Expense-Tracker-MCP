package com.maycon.expenses_api.dto;

import com.maycon.expenses_api.model.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponseDTO(
    Long id,
    String description,
    BigDecimal amount,
    String category,
    LocalDate date
) {
    public static ExpenseResponseDTO fromEntity(Expense expense) {
        return new ExpenseResponseDTO(
            expense.getId(),
            expense.getDescription(),
            expense.getAmount(),
            expense.getCategory(),
            expense.getDate()
        );
    }
}

