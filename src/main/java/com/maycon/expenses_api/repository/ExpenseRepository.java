package com.maycon.expenses_api.repository;

import com.maycon.expenses_api.dto.CategorySummaryDTO;
import com.maycon.expenses_api.model.Expense;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategoryIgnoreCase(String category);

    @Query("SELECT new com.maycon.expenses_api.dto.CategorySummaryDTO(e.category, SUM(e.amount)) FROM Expense e GROUP BY e.category")
    List<CategorySummaryDTO> getSummaryByCategory();
}

