import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  set token(value: string) {
    localStorage.setItem("token", value);
  }

  get token(): string | null {
    return localStorage.getItem("token");
  }
}
