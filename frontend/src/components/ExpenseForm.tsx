import { useState, type FormEvent } from 'react';
import { ApiError, createExpense } from '../api';

interface ExpenseFormProps {
  onExpenseCreated: () => void;
}

function getTodayString(): string {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function ExpenseForm({ onExpenseCreated }: ExpenseFormProps) {
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [category, setCategory] = useState('');
  const [date, setDate] = useState(getTodayString());

  const [clientErrors, setClientErrors] = useState<Record<string, string>>({});
  const [apiErrors, setApiErrors] = useState<Record<string, string>>({});
  const [generalError, setGeneralError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validate = (): boolean => {
    const errors: Record<string, string> = {};

    if (!description.trim()) {
      errors.description = 'A descrição é obrigatória.';
    }

    const parsedAmount = Number(amount);
    if (!amount || Number.isNaN(parsedAmount) || parsedAmount <= 0) {
      errors.amount = 'O valor deve ser maior que zero.';
    }

    if (!category.trim()) {
      errors.category = 'A categoria é obrigatória.';
    }

    if (!date) {
      errors.date = 'A data é obrigatória.';
    }

    setClientErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setGeneralError(null);
    setApiErrors({});

    if (!validate()) {
      return;
    }

    setIsSubmitting(true);

    try {
      await createExpense({
        description: description.trim(),
        amount: Number(amount),
        category: category.trim(),
        date,
      });

      setDescription('');
      setAmount('');
      setCategory('');
      setDate(getTodayString());
      setClientErrors({});
      onExpenseCreated();
    } catch (err: unknown) {
      if (err instanceof ApiError) {
        if (err.fieldErrors) {
          setApiErrors(err.fieldErrors);
        } else {
          setGeneralError(err.message);
        }
      } else if (err instanceof Error) {
        setGeneralError(err.message);
      } else {
        setGeneralError('Ocorreu um erro inesperado ao salvar a despesa.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const getFieldError = (field: string): string | undefined => {
    return clientErrors[field] || apiErrors[field];
  };

  return (
    <form className="expense-form card" onSubmit={handleSubmit} noValidate>
      <h2>Nova Despesa</h2>

      {generalError && (
        <div className="alert alert-error" role="alert">
          {generalError}
        </div>
      )}

      <div className="form-group">
        <label htmlFor="expense-description">Descrição</label>
        <input
          id="expense-description"
          type="text"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Ex: Almoço, Metrô, Supermercado"
          disabled={isSubmitting}
        />
        {getFieldError('description') && (
          <span className="field-error">{getFieldError('description')}</span>
        )}
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="expense-amount">Valor (R$)</label>
          <input
            id="expense-amount"
            type="number"
            step="0.01"
            min="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0,00"
            disabled={isSubmitting}
          />
          {getFieldError('amount') && (
            <span className="field-error">{getFieldError('amount')}</span>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="expense-category">Categoria</label>
          <input
            id="expense-category"
            type="text"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            placeholder="Ex: Alimentação, Transporte, Lazer"
            disabled={isSubmitting}
          />
          {getFieldError('category') && (
            <span className="field-error">{getFieldError('category')}</span>
          )}
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="expense-date">Data</label>
        <input
          id="expense-date"
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          disabled={isSubmitting}
        />
        {getFieldError('date') && (
          <span className="field-error">{getFieldError('date')}</span>
        )}
      </div>

      <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
        {isSubmitting ? 'Registrando...' : 'Adicionar Despesa'}
      </button>
    </form>
  );
}
