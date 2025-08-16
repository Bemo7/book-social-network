import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { KeyCloakService } from './shared/services/keycloak/keycloak.service';

export const kcFactory = (keycloakService: KeyCloakService) => {
  console.log('Creating Keycloak factory');
  return () => keycloakService.init();
};

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
  standalone: true
})
export class App {
  protected readonly title = signal('boot-network-ui');
}
