import { Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component'
import {canActivateAuthRole} from './auth.guard';
import {RideComponent} from './components/ride-component/ride-component';
import {DriveComponent} from './components/drive-component/drive-component';
import {RideListComponent} from './components/ride-list-component/ride-list-component';
import {YourRideComponent} from './components/your-ride-component/your-ride-component';

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
    path: "**",
    component: HomeComponent
  }
];
