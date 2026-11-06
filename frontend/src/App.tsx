import { useCallback, useEffect, useState } from 'react';
import { getSummary, listExpenses } from './api';
import { CategorySummary } from './components/CategorySummary';
import { ExpenseForm } from './components/ExpenseForm';
import { ExpenseList } from './components/ExpenseList';
import type { CategorySummary as CategorySummaryType, ExpenseResponse } from './types';
import './App.css';

export function App() {
  const [expenses, setExpenses] = useState<ExpenseResponse[]>([]);
  const [expensesLoading, setExpensesLoading] = useState(false);
  const [expensesError, setExpensesError] = useState<string | null>(null);

  const [summary, setSummary] = useState<CategorySummaryType[]>([]);
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [summaryError, setSummaryError] = useState<string | null>(null);

  const [categoryFilter, setCategoryFilter] = useState('');

  const fetchExpenses = useCallback(async (category?: string) => {
    setExpensesLoading(true);
    setExpensesError(null);
    try {
      const data = await listExpenses(category);
      setExpenses(data);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Falha ao carregar despesas.';
      setExpensesError(message);
    } finally {
      setExpensesLoading(false);
    }
  }, []);

  const fetchSummary = useCallback(async () => {
    setSummaryLoading(true);
    setSummaryError(null);
    try {
      const data = await getSummary();
      setSummary(data);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Falha ao carregar resumo de categorias.';
      setSummaryError(message);
    } finally {
      setSummaryLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;

    async function loadData() {
      setExpensesLoading(true);
      setExpensesError(null);
      try {
        const data = await listExpenses(categoryFilter);
        if (active) setExpenses(data);
      } catch (err: unknown) {
        if (active) {
          setExpensesError(err instanceof Error ? err.message : 'Falha ao carregar despesas.');
        }
      } finally {
        if (active) setExpensesLoading(false);
      }
    }

    void loadData();

    return () => {
      active = false;
    };
  }, [categoryFilter]);

  useEffect(() => {
    let active = true;

    async function loadSummary() {
      setSummaryLoading(true);
      setSummaryError(null);
      try {
        const data = await getSummary();
        if (active) setSummary(data);
      } catch (err: unknown) {
        if (active) {
          setSummaryError(err instanceof Error ? err.message : 'Falha ao carregar resumo de categorias.');
        }
      } finally {
        if (active) setSummaryLoading(false);
      }
    }

    void loadSummary();

    return () => {
      active = false;
    };
  }, []);

  const handleExpenseCreated = () => {
    void fetchExpenses(categoryFilter);
    void fetchSummary();
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <div className="header-content">
          <h1>Expense Tracker</h1>
          <p className="subtitle">Controle de Despesas &amp; Servidor MCP</p>
        </div>
      </header>

      <main className="app-main">
        <div className="sidebar-column">
          <ExpenseForm onExpenseCreated={handleExpenseCreated} />
          <CategorySummary
            summary={summary}
            loading={summaryLoading}
            error={summaryError}
            onRetry={fetchSummary}
          />
        </div>

        <div className="content-column">
          <ExpenseList
            expenses={expenses}
            loading={expensesLoading}
            error={expensesError}
            categoryFilter={categoryFilter}
            onFilterChange={setCategoryFilter}
            onRetry={() => void fetchExpenses(categoryFilter)}
          />
        </div>
      </main>
    </div>
  );
}

export default App;
