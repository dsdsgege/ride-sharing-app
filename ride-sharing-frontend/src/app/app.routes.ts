import { Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component'
import {createAuthGuard} from 'keycloak-angular';

export const routes: Routes = [
  {
    path: "**",
    component: HomeComponent,
    canActivate: [createAuthGuard]
  }
];
