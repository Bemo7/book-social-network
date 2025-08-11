import { Component } from '@angular/core';
import { CodeInputModule } from 'angular-code-input';
import { AuthenticationService } from '../../../../openapi/api/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-activate-account',
  imports: [CodeInputModule, CommonModule, RouterModule],
  templateUrl: './activate-account.html',
  styleUrl: './activate-account.scss'
})
export class ActivateAccount {
  message: String = "";
  isOkay = false;
  hasSubmitted = false;

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) { }


  // this called every time when user changed the code
  onCodeChanged(code: string) {
  }

  // this called only if user entered full code
  onCodeCompleted(code: string) {
    this.authenticationService.confirm(code).subscribe({
      next: (response) => {
        this.message = "Account activated successfully!";
        this.isOkay = true;
        this.hasSubmitted = true;
      },
      error: (error) => {
        if (error instanceof HttpErrorResponse) {
          this.message = error.error.error || "An error occurred during activation.";
        }
        this.isOkay = false;
        this.hasSubmitted = true;
      }
    });
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
