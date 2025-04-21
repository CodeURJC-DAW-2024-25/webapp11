import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private errorTitle: string | null = null;
  private errorMessage: string | null = null;

  constructor() {}

  setError(title: string, message: string): void {
    this.errorTitle = title;
    this.errorMessage = message;
  }

  getErrorTitle(): string | null {
    return this.errorTitle;
  }

  getErrorMessage(): string | null {
    return this.errorMessage;
  }

  clearError(): void {
    this.errorTitle = null;
    this.errorMessage = null;
  }
}
