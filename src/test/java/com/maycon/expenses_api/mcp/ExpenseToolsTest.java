package com.maycon.expenses_api.mcp;

import com.maycon.expenses_api.dto.CategorySummaryDTO;
import com.maycon.expenses_api.dto.ExpenseRequestDTO;
import com.maycon.expenses_api.dto.ExpenseResponseDTO;
import com.maycon.expenses_api.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseToolsTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseTools expenseTools;

    @Test
    @DisplayName("Should successfully register expense when all inputs including date are valid")
    void shouldRegisterExpenseWithExplicitDate() {
        ExpenseResponseDTO mockResponse = new ExpenseResponseDTO(
            1L,
            "Almoço",
            new BigDecimal("35.50"),
            "Alimentação",
            LocalDate.of(2026, 11, 5)
        );

        when(expenseService.create(any(ExpenseRequestDTO.class))).thenReturn(mockResponse);

        ExpenseResponseDTO result = expenseTools.registerExpense(
            "Almoço",
            new BigDecimal("35.50"),
            "Alimentação",
            "2026-11-05"
        );

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.description()).isEqualTo("Almoço");
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("35.50"));

        ArgumentCaptor<ExpenseRequestDTO> captor = ArgumentCaptor.forClass(ExpenseRequestDTO.class);
        verify(expenseService).create(captor.capture());
        ExpenseRequestDTO captured = captor.getValue();
        assertThat(captured.description()).isEqualTo("Almoço");
        assertThat(captured.amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(captured.category()).isEqualTo("Alimentação");
        assertThat(captured.date()).isEqualTo(LocalDate.of(2026, 11, 5));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should default date to today when date parameter is null or blank")
    void shouldDefaultDateToTodayWhenDateIsNullOrEmpty(String emptyDate) {
        ExpenseResponseDTO mockResponse = new ExpenseResponseDTO(
            2L,
            "Farmácia",
            new BigDecimal("50.00"),
            "Saúde",
            LocalDate.now()
        );

        when(expenseService.create(any(ExpenseRequestDTO.class))).thenReturn(mockResponse);

        ExpenseResponseDTO result = expenseTools.registerExpense(
            "Farmácia",
            new BigDecimal("50.00"),
            "Saúde",
            emptyDate
        );

        assertThat(result).isNotNull();
        ArgumentCaptor<ExpenseRequestDTO> captor = ArgumentCaptor.forClass(ExpenseRequestDTO.class);
        verify(expenseService).create(captor.capture());
        assertThat(captor.getValue().date()).isEqualTo(LocalDate.now());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw IllegalArgumentException when description is empty or blank")
    void shouldThrowWhenDescriptionIsBlank(String invalidDescription) {
        assertThatThrownBy(() -> expenseTools.registerExpense(
            invalidDescription,
            new BigDecimal("25.00"),
            "Transporte",
            "2026-11-05"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description must not be empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amount is null")
    void shouldThrowWhenAmountIsNull() {
        assertThatThrownBy(() -> expenseTools.registerExpense(
            "Uber",
            null,
            "Transporte",
            "2026-11-05"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Amount must be greater than zero");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-0.01", "-50.00"})
    @DisplayName("Should throw IllegalArgumentException when amount is less than or equal to zero")
    void shouldThrowWhenAmountIsZeroOrNegative(String invalidAmount) {
        assertThatThrownBy(() -> expenseTools.registerExpense(
            "Uber",
            new BigDecimal(invalidAmount),
            "Transporte",
            "2026-11-05"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Amount must be greater than zero");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when category is empty or blank")
    void shouldThrowWhenCategoryIsBlank(String invalidCategory) {
        assertThatThrownBy(() -> expenseTools.registerExpense(
            "Internet",
            new BigDecimal("100.00"),
            invalidCategory,
            "2026-11-05"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Category must not be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"05/11/2026", "2026/11/05", "invalid-date", "2026-13-40"})
    @DisplayName("Should throw IllegalArgumentException when date format is invalid")
    void shouldThrowWhenDateFormatIsInvalid(String invalidDate) {
        assertThatThrownBy(() -> expenseTools.registerExpense(
            "Cinema",
            new BigDecimal("40.00"),
            "Lazer",
            invalidDate
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid date format. Expected yyyy-MM-dd");
    }

    @Test
    @DisplayName("Should list expenses and limit to 50 most recent")
    void shouldListExpensesLimitedTo50MostRecent() {
        List<ExpenseResponseDTO> mockList = new ArrayList<>();
        // Create 60 expenses with dates from day 1 to 60
        for (long i = 1; i <= 60; i++) {
            mockList.add(new ExpenseResponseDTO(
                i,
                "Expense " + i,
                new BigDecimal("10.00"),
                "Geral",
                LocalDate.of(2026, 1, 1).plusDays(i)
            ));
        }

        when(expenseService.findAll(null)).thenReturn(mockList);

        List<ExpenseResponseDTO> result = expenseTools.listExpenses(null);

        assertThat(result).hasSize(50);
        // Most recent date should come first
        assertThat(result.get(0).id()).isEqualTo(60L);
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(2026, 1, 1).plusDays(60));
        assertThat(result.get(49).id()).isEqualTo(11L);
    }

    @Test
    @DisplayName("Should list expenses filtered by category")
    void shouldListExpensesFilteredByCategory() {
        List<ExpenseResponseDTO> mockList = List.of(
            new ExpenseResponseDTO(1L, "Metrô", new BigDecimal("5.00"), "Transporte", LocalDate.of(2026, 11, 5))
        );

        when(expenseService.findAll("Transporte")).thenReturn(mockList);

        List<ExpenseResponseDTO> result = expenseTools.listExpenses("Transporte");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Metrô");
        verify(expenseService).findAll("Transporte");
    }

    @Test
    @DisplayName("Should summarize spending by category")
    void shouldSummarizeByCategory() {
        List<CategorySummaryDTO> mockSummary = List.of(
            new CategorySummaryDTO("Alimentação", new BigDecimal("250.00")),
            new CategorySummaryDTO("Transporte", new BigDecimal("120.00"))
        );

        when(expenseService.getSummaryByCategory()).thenReturn(mockSummary);

        List<CategorySummaryDTO> result = expenseTools.summarizeByCategory();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).category()).isEqualTo("Alimentação");
        assertThat(result.get(0).total()).isEqualByComparingTo(new BigDecimal("250.00"));
        verify(expenseService).getSummaryByCategory();
    }
}
