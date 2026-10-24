package com.maycon.expenses_api.service;

import com.maycon.expenses_api.dto.CategorySummaryDTO;
import com.maycon.expenses_api.dto.ExpenseRequestDTO;
import com.maycon.expenses_api.dto.ExpenseResponseDTO;
import com.maycon.expenses_api.model.Expense;
import com.maycon.expenses_api.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    @DisplayName("Should successfully create and persist an expense")
    void shouldCreateExpenseSuccessfully() {
        ExpenseRequestDTO request = new ExpenseRequestDTO(
            "Almoço",
            new BigDecimal("35.50"),
            "Alimentação",
            LocalDate.of(2026, 10, 24)
        );

        Expense savedEntity = new Expense(
            1L,
            "Almoço",
            new BigDecimal("35.50"),
            "Alimentação",
            LocalDate.of(2026, 10, 24)
        );

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedEntity);

        ExpenseResponseDTO response = expenseService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.description()).isEqualTo("Almoço");
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(response.category()).isEqualTo("Alimentação");
        assertThat(response.date()).isEqualTo(LocalDate.of(2026, 10, 24));

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        Expense captured = captor.getValue();
        assertThat(captured.getDescription()).isEqualTo("Almoço");
        assertThat(captured.getAmount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(captured.getCategory()).isEqualTo("Alimentação");
        assertThat(captured.getDate()).isEqualTo(LocalDate.of(2026, 10, 24));
    }

    @Test
    @DisplayName("Should return all expenses when category filter is null")
    void shouldFindAllExpensesWhenCategoryIsNull() {
        List<Expense> mockExpenses = List.of(
            new Expense(1L, "Mercado", new BigDecimal("120.00"), "Alimentação", LocalDate.of(2026, 10, 20)),
            new Expense(2L, "Gasolina", new BigDecimal("80.00"), "Transporte", LocalDate.of(2026, 10, 21))
        );

        when(expenseRepository.findAll()).thenReturn(mockExpenses);

        List<ExpenseResponseDTO> result = expenseService.findAll(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).description()).isEqualTo("Mercado");
        assertThat(result.get(1).description()).isEqualTo("Gasolina");
        verify(expenseRepository).findAll();
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    @DisplayName("Should return all expenses when category filter is empty or blank")
    void shouldFindAllExpensesWhenCategoryIsBlank() {
        List<Expense> mockExpenses = List.of(
            new Expense(1L, "Cinema", new BigDecimal("45.00"), "Lazer", LocalDate.of(2026, 10, 22))
        );

        when(expenseRepository.findAll()).thenReturn(mockExpenses);

        List<ExpenseResponseDTO> result = expenseService.findAll("   ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo("Lazer");
        verify(expenseRepository).findAll();
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    @DisplayName("Should return filtered expenses when category is provided")
    void shouldFindExpensesByCategory() {
        List<Expense> mockExpenses = List.of(
            new Expense(1L, "Uber", new BigDecimal("25.00"), "Transporte", LocalDate.of(2026, 10, 23))
        );

        when(expenseRepository.findByCategoryIgnoreCase("transporte")).thenReturn(mockExpenses);

        List<ExpenseResponseDTO> result = expenseService.findAll("  transporte  ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Uber");
        assertThat(result.get(0).category()).isEqualTo("Transporte");
        verify(expenseRepository).findByCategoryIgnoreCase("transporte");
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    @DisplayName("Should return total summary grouped by category")
    void shouldReturnSummaryByCategory() {
        List<CategorySummaryDTO> mockSummary = List.of(
            new CategorySummaryDTO("Alimentação", new BigDecimal("155.50")),
            new CategorySummaryDTO("Transporte", new BigDecimal("105.00"))
        );

        when(expenseRepository.getSummaryByCategory()).thenReturn(mockSummary);

        List<CategorySummaryDTO> result = expenseService.getSummaryByCategory();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).category()).isEqualTo("Alimentação");
        assertThat(result.get(0).total()).isEqualByComparingTo(new BigDecimal("155.50"));
        assertThat(result.get(1).category()).isEqualTo("Transporte");
        assertThat(result.get(1).total()).isEqualByComparingTo(new BigDecimal("105.00"));
        verify(expenseRepository).getSummaryByCategory();
        verifyNoMoreInteractions(expenseRepository);
    }
}

