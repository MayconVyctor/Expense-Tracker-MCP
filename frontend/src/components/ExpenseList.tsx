import type { ExpenseResponse } from '../types';

interface ExpenseListProps {
  expenses: ExpenseResponse[];
  loading: boolean;
  error: string | null;
  categoryFilter: string;
  onFilterChange: (category: string) => void;
  onRetry: () => void;
}

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
});

function formatDate(dateStr: string): string {
  const parts = dateStr.split('-');
  if (parts.length === 3) {
    const [year, month, day] = parts;
    return `${day}/${month}/${year}`;
  }
  return dateStr;
}

export function ExpenseList({
  expenses,
  loading,
  error,
  categoryFilter,
  onFilterChange,
  onRetry,
}: ExpenseListProps) {
  return (
    <section className="expense-list card">
      <div className="list-header">
        <h2>Despesas Registradas</h2>
        <div className="filter-group">
          <label htmlFor="category-filter">Filtrar por Categoria:</label>
          <input
            id="category-filter"
            type="text"
            value={categoryFilter}
            onChange={(e) => onFilterChange(e.target.value)}
            placeholder="Todas as categorias"
          />
          {categoryFilter && (
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={() => onFilterChange('')}
            >
              Limpar
            </button>
          )}
        </div>
      </div>

      {loading && <p className="status-msg">Carregando despesas...</p>}

      {!loading && error && (
        <div className="alert alert-error">
          <p>{error}</p>
          <button type="button" className="btn btn-secondary btn-sm" onClick={onRetry}>
            Tentar novamente
          </button>
        </div>
      )}

      {!loading && !error && expenses.length === 0 && (
        <p className="status-msg empty-msg">
          {categoryFilter.trim()
            ? `Nenhuma despesa encontrada para a categoria "${categoryFilter}".`
            : 'Nenhuma despesa cadastrada ainda.'}
        </p>
      )}

      {!loading && !error && expenses.length > 0 && (
        <div className="table-responsive">
          <table className="expenses-table">
            <thead>
              <tr>
                <th>Descrição</th>
                <th>Categoria</th>
                <th>Data</th>
                <th className="text-right">Valor</th>
              </tr>
            </thead>
            <tbody>
              {expenses.map((expense) => (
                <tr key={expense.id}>
                  <td>{expense.description}</td>
                  <td>
                    <span className="badge">{expense.category}</span>
                  </td>
                  <td>{formatDate(expense.date)}</td>
                  <td className="text-right text-bold">
                    {currencyFormatter.format(expense.amount)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
