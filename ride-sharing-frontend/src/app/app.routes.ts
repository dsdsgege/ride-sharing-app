import { Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component'
import {canActivateAuthRole} from './auth.guard';
import {RideComponent} from './components/ride/ride-component/ride-component';
import {DriveComponent} from './components/drive-component/drive-component';
import {RideListComponent} from './components/ride/ride-list-component/ride-list-component';
import {YourRideComponent} from './components/ride/your-ride-component/your-ride-component';

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
    path: "ride/ride-list",
    component: RideListComponent,
  },
  {
    path: "ride/ride-list/your-ride",
    component: YourRideComponent,
  },
  {
    path: "profile",
    component: HomeComponent,
    canActivate: [canActivateAuthRole],
    data: {
      role: 'USER'
    }
  },
  {
    path: "**",
    component: HomeComponent
  }
];
