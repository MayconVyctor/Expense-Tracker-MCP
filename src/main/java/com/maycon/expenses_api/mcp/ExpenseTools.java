package com.maycon.expenses_api.mcp;

import com.maycon.expenses_api.dto.CategorySummaryDTO;
import com.maycon.expenses_api.dto.ExpenseRequestDTO;
import com.maycon.expenses_api.dto.ExpenseResponseDTO;
import com.maycon.expenses_api.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ExpenseTools {

    private final ExpenseService expenseService;

    public ExpenseTools(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @McpTool(
        name = "registerExpense",
        description = "Records a new expense with description, positive amount in BRL, category, and optional date. Use this tool whenever a user wants to log, add, or create a new expense."
    )
    public ExpenseResponseDTO registerExpense(
        @McpToolParam(
            description = "Clear description of the expense item or service (e.g., 'Almoço com equipe', 'Uber para o aeroporto'). Must not be empty.",
            required = true
        )
        String description,

        @McpToolParam(
            description = "Positive monetary amount in Brazilian Reais (BRL), e.g., 35.50. Must be strictly greater than zero.",
            required = true
        )
        BigDecimal amount,

        @McpToolParam(
            description = "Category of the expense (e.g., 'Alimentação', 'Transporte', 'Lazer', 'Saúde'). Will be automatically normalized with the first letter capitalized.",
            required = true
        )
        String category,

        @McpToolParam(
            description = "Optional expense date in ISO-8601 format yyyy-MM-dd (e.g., '2026-11-05'). If omitted or null, defaults to today's date.",
            required = false
        )
        String date
    ) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description must not be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category must not be empty");
        }

        LocalDate expenseDate;
        if (date == null || date.trim().isEmpty()) {
            expenseDate = LocalDate.now();
        } else {
            try {
                expenseDate = LocalDate.parse(date.trim());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd: " + date, e);
            }
        }

        ExpenseRequestDTO dto = new ExpenseRequestDTO(
            description.trim(),
            amount,
            category.trim(),
            expenseDate
        );

        return expenseService.create(dto);
    }

    @McpTool(
        name = "listExpenses",
        description = "Retrieves a list of up to 50 most recent expenses, optionally filtered by category. Use this tool when the user wants to check, view, or search recorded expenses."
    )
    public List<ExpenseResponseDTO> listExpenses(
        @McpToolParam(
            description = "Optional category name to filter expenses (e.g., 'Transporte'). Case-insensitive. If omitted or null, all categories are returned.",
            required = false
        )
        String category
    ) {
        List<ExpenseResponseDTO> expenses = expenseService.findAll(category);
        return expenses.stream()
            .sorted(Comparator.comparing(ExpenseResponseDTO::date, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(ExpenseResponseDTO::id, Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(50)
            .toList();
    }

    @McpTool(
        name = "summarizeByCategory",
        description = "Calculates and returns the aggregated total expenditure grouped by category. Use this tool when the user asks for a spending report, breakdown, or category totals."
    )
    public List<CategorySummaryDTO> summarizeByCategory() {
        return expenseService.getSummaryByCategory();
    }
}
