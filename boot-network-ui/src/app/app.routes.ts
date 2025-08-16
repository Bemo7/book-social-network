import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { ActivateAccount } from './pages/activate-account/activate-account';
import { authGuard } from './shared/guard/auth-guard';

export const routes: Routes = [
    // { path: 'login', component: Login, canActivate: [authGuard] },
    // { path: 'register', component: Register, canActivate: [authGuard] },
    { path: 'activate-account', component: ActivateAccount, canActivate: [authGuard] }
];
