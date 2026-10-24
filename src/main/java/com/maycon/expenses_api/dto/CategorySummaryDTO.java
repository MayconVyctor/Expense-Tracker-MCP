package com.maycon.expenses_api.dto;

import java.math.BigDecimal;

public record CategorySummaryDTO(
    String category,
    BigDecimal total
) {}

