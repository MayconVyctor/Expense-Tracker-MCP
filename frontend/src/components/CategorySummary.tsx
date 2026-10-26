import type { CategorySummary as CategorySummaryType } from '../types';

interface CategorySummaryProps {
  summary: CategorySummaryType[];
  loading: boolean;
  error: string | null;
  onRetry?: () => void;
}

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
});

export function CategorySummary({
  summary,
  loading,
  error,
  onRetry,
}: CategorySummaryProps) {
  // Encontra o maior valor apenas para calcular a proporção da barra
  const maxTotal = summary.reduce((max, item) => {
    const val = Number(item.total);
    return val > max ? val : max;
  }, 0);

  return (
    <section className="category-summary card">
      <h2>Resumo por Categoria</h2>

      {loading && <p className="status-msg">Carregando resumo...</p>}

      {!loading && error && (
        <div className="alert alert-error">
          <p>{error}</p>
          {onRetry && (
            <button type="button" className="btn btn-secondary btn-sm" onClick={onRetry}>
              Tentar novamente
            </button>
          )}
        </div>
      )}

      {!loading && !error && summary.length === 0 && (
        <p className="status-msg empty-msg">Nenhuma despesa para resumir no momento.</p>
      )}

      {!loading && !error && summary.length > 0 && (
        <div className="chart-container">
          {summary.map((item) => {
            const widthPercentage = maxTotal > 0 ? (Number(item.total) / maxTotal) * 100 : 0;

            return (
              <div key={item.category} className="chart-item">
                <div className="chart-labels">
                  <span className="category-name">{item.category}</span>
                  <span className="category-total">
                    {currencyFormatter.format(Number(item.total))}
                  </span>
                </div>
                <div
                  className="chart-track"
                  role="progressbar"
                  aria-label={`Total gasto em ${item.category}`}
                  aria-valuenow={Number(item.total)}
                  aria-valuemin={0}
                  aria-valuemax={maxTotal}
                >
                  <div
                    className="chart-bar"
                    style={{ width: `${Math.max(widthPercentage, 4)}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      )}
    </section>
  );
}
