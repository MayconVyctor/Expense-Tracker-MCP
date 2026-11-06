package com.maycon.expenses_api.service;

import com.maycon.expenses_api.dto.CategorySummaryDTO;
import com.maycon.expenses_api.dto.ExpenseRequestDTO;
import com.maycon.expenses_api.dto.ExpenseResponseDTO;
import com.maycon.expenses_api.model.Expense;
import com.maycon.expenses_api.repository.ExpenseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseResponseDTO create(ExpenseRequestDTO dto) {
        Expense expense = new Expense(
            dto.description().trim(),
            dto.amount(),
            normalizeCategory(dto.category()),
            dto.date()
        );
        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseResponseDTO.fromEntity(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> findAll(String category) {
        List<Expense> expenses;
        if (category != null && !category.trim().isEmpty()) {
            String normalizedCategory = normalizeCategory(category);
            expenses = expenseRepository.findByCategoryIgnoreCase(normalizedCategory);
        } else {
            expenses = expenseRepository.findAll();
        }
        return expenses.stream()
            .map(ExpenseResponseDTO::fromEntity)
            .toList();
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return null;
        }
        String trimmed = category.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        String lower = trimmed.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryDTO> getSummaryByCategory() {
        return expenseRepository.getSummaryByCategory();
    }
}

