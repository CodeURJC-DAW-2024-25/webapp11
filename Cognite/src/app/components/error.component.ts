import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { ErrorService } from '../services/error.service';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['../app.component.css']
})
export class ErrorComponent {
  errorTitle: string | null = '';
  errorMessage: string | null = '';

  constructor(private location: Location, private errorService: ErrorService) {
    this.errorTitle = this.errorService.getErrorTitle();
    this.errorMessage = this.errorService.getErrorMessage();
  }

  goBack(): void {
    this.location.back();
  }
}
