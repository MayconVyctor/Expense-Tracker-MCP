package com.maycon.expenses_api.controller;

import com.maycon.expenses_api.dto.CategorySummaryDTO;
import com.maycon.expenses_api.dto.ExpenseRequestDTO;
import com.maycon.expenses_api.dto.ExpenseResponseDTO;
import com.maycon.expenses_api.service.ExpenseService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> create(@Valid @RequestBody ExpenseRequestDTO request) {
        ExpenseResponseDTO created = expenseService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> findAll(
            @RequestParam(required = false) String category) {
        List<ExpenseResponseDTO> expenses = expenseService.findAll(category);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<CategorySummaryDTO>> getSummary() {
        List<CategorySummaryDTO> summary = expenseService.getSummaryByCategory();
        return ResponseEntity.ok(summary);
    }
}

