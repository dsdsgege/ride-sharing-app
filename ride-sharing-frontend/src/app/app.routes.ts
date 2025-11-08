import { Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component'
import {canActivateAuthRole} from './auth.guard';
import {RideComponent} from './components/ride-component/ride-component';
import {DriveComponent} from './components/drive-component/drive-component';

export const routes: Routes = [
  {
    path: "home",
    component: HomeComponent,
  },
  {
    path: "drive",
    component: DriveComponent,
  },
  {
    path: "ride",
    component: RideComponent,
  },
  {
    path: "profile",
    component: HomeComponent,
    canActivate: [canActivateAuthRole],
    data: {
      role: 'USER'
    }
  }
];
