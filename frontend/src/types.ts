export interface ExpenseRequest {
  description: string;
  amount: number;
  category: string;
  date: string;
}

export interface ExpenseResponse {
  id: number;
  description: string;
  amount: number;
  category: string;
  date: string;
}

export interface CategorySummary {
  category: string;
  total: number;
}

export interface ApiValidationErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  errors?: Record<string, string>;
}
