import { Component, OnInit } from '@angular/core';
import { AuthenticationRequest } from '../../../../openapi/model/authenticationRequest';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../../../openapi/api/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TokenService } from '../../shared/services/token/token.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  standalone: true
})
export class Login implements OnInit {

  loginForm!: FormGroup;

  authRequest: AuthenticationRequest = {
    email: '',
    password: ''
  };

  errorMsg: Array<String> = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authenticationService: AuthenticationService,
    private tokenService: TokenService
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onLogin(): void {
    this.errorMsg = [];
    this.authenticationService.authenticate(
      {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password
      }
    ).subscribe(
      {
        next: (response)=> {
          //TODO: save token 
          this.router.navigate(['/books']);
          this.tokenService.token = response.token;
        },
        error: (error)=> {
          if (error instanceof HttpErrorResponse) {
            if (error.error.validationErrors) {
              this.errorMsg.push(...error.error.validationErrors);
            } else {
              this.errorMsg.push(error.error.error);
            }
          }
        }
      }
    )
  }

  onRegister(): void {
    this.router.navigate(['/register']);
  }
}
