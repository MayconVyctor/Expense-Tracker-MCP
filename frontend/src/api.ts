import type {
  ApiValidationErrorResponse,
  CategorySummary,
  ExpenseRequest,
  ExpenseResponse,
} from './types';

export class ApiError extends Error {
  readonly status: number;
  readonly fieldErrors?: Record<string, string>;

  constructor(status: number, message: string, fieldErrors?: Record<string, string>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorMessage = `Erro HTTP ${response.status}`;
    let fieldErrors: Record<string, string> | undefined;

    try {
      const errorData: ApiValidationErrorResponse = await response.json();
      if (errorData.errors && Object.keys(errorData.errors).length > 0) {
        errorMessage = Object.values(errorData.errors).join(', ');
        fieldErrors = errorData.errors;
      } else if (errorData.message) {
        errorMessage = errorData.message;
      }
    } catch {
      // Ignora falha de parse caso a resposta não seja JSON
    }

    throw new ApiError(response.status, errorMessage, fieldErrors);
  }

  return response.json() as Promise<T>;
}

export async function createExpense(data: ExpenseRequest): Promise<ExpenseResponse> {
  const response = await fetch('/expenses', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  return handleResponse<ExpenseResponse>(response);
}

export async function listExpenses(category?: string): Promise<ExpenseResponse[]> {
  const trimmed = category?.trim();
  const url = trimmed ? `/expenses?category=${encodeURIComponent(trimmed)}` : '/expenses';
  const response = await fetch(url);

  return handleResponse<ExpenseResponse[]>(response);
}

export async function getSummary(): Promise<CategorySummary[]> {
  const response = await fetch('/expenses/summary');

  return handleResponse<CategorySummary[]>(response);
}
