import { Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component'
import {canActivateAuthRole} from './auth.guard';

export const routes: Routes = [
  {
    path: "home",
    component: HomeComponent,
    canActivate: [canActivateAuthRole],
    data: {
      role: 'USER'
    }
  },
  {
    path: "profile",
    component: HomeComponent,
    canActivate: [canActivateAuthRole],
    data: {
      role: 'ADMIN'
    }
  }
];
