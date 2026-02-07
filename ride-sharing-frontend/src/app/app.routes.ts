import { Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component'
import {canActivateAuthRole} from './auth.guard';
import {RideComponent} from './components/ride/ride-component/ride-component';
import {DriveComponent} from './components/drive/drive-component/drive-component';
import {RideListComponent} from './components/ride/ride-list-component/ride-list-component';
import {YourRideComponent} from './components/ride/your-ride-component/your-ride-component';
import {ProfileComponent} from './components/profile-component/profile-component';
import {AcceptPassengerComponent} from './components/drive/accept-passenger-component/accept-passenger-component';

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
    component: ProfileComponent,
    canActivate: [canActivateAuthRole],
    data: {
      role: 'USER'
    }
  },
  {
    path: "drive/accept-passenger",
    component: AcceptPassengerComponent,
    canActivate: [canActivateAuthRole]
  },
  {
    path: "**",
    component: HomeComponent
  }
];
