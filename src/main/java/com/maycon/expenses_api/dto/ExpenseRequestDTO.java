package com.maycon.expenses_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequestDTO(
    @NotBlank(message = "A descrição não pode ser vazia")
    String description,

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    BigDecimal amount,

    @NotBlank(message = "A categoria não pode ser vazia")
    String category,

    @NotNull(message = "A data é obrigatória")
    LocalDate date
) {}

