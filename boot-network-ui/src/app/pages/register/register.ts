import { Component, OnInit } from '@angular/core';
import { RegistrationRequest } from '../../../../openapi/model/registrationRequest';
import { AuthenticationService } from '../../../../openapi/api/authentication.service';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
  standalone: true
})
export class Register implements OnInit{
  registrationRequest: RegistrationRequest = {
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  };

  registerForm!: FormGroup;

  errorMsg: Array<String> = [];

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.registerForm = new FormGroup({
      email: new FormControl(this.registrationRequest.email, [Validators.required, Validators.email]),
      password: new FormControl(this.registrationRequest.password, [Validators.required, Validators.minLength(8)]),
      firstName: new FormControl(this.registrationRequest.firstName, [Validators.required]),
      lastName: new FormControl(this.registrationRequest.lastName, [Validators.required])
    });
  }

  onRegister(): void {
    this.authenticationService.register(this.registerForm.value).subscribe({
      next: (response) => {
        this.router.navigate(['/activate-account']);
      },
      error: (error) => {
        if (error instanceof HttpErrorResponse) {
          if (error.error.validationErrors) {
            this.errorMsg.push(...error.error.validationErrors);
          } else {
            this.errorMsg.push(error.error.error);
          }
        }
      }
    });
  }

  onLogin(): void {
    this.router.navigate(['/login']);
  }
}
