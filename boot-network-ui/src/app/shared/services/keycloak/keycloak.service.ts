import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from '../../model/UserProfile';

@Injectable({
  providedIn: 'root'
})
export class KeyCloakService {
  _keycloak: Keycloak | undefined;
  _userProfile: UserProfile | undefined;

  get keycloak(): Keycloak {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090/auth',
        realm: 'book-social-network',
        clientId: 'bsn-frontend'
      })
    }
    return this._keycloak;
  }

  get userProfile(): UserProfile | undefined {
    return this._userProfile;
  }

  constructor() { }

  async init(): Promise<void> {
    this._keycloak = new Keycloak({
      url: 'http://localhost:9090/',
      realm: 'book-social-network',
      clientId: 'bsn-frontend'
    });

    const authenticated = await this.keycloak?.init({ onLoad: 'login-required' });

    if (authenticated) {
      console.log('User is authenticated');
      this._userProfile = (await this.keycloak.loadUserProfile()) as UserProfile;
      console.log(this.keycloak.token);
    } else {
      console.log('User is not authenticated');
    }

    return Promise.resolve();
  }

  login(): Promise<void> {
    return this.keycloak.login();
  }

  logout(): Promise<void> {
    return this.keycloak.logout({redirectUri: '/'});
  }
}
