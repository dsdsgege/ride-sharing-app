import { Component } from '@angular/core';
import {Breadcrumb} from 'primeng/breadcrumb';

@Component({
  selector: 'app-your-ride-component',
  imports: [
    Breadcrumb
  ],
  templateUrl: './your-ride-component.html',
  standalone: true,
  styleUrl: './your-ride-component.scss'
})
export class YourRideComponent {
  items = [
    {"label": "Search rides", routerLink: "/ride"},
    {"label": "Choose your ride", routerLink: "/ride/ride-list"},
    {"label": "Your ride", routerLink: "/ride/ride-list/your-ride"},
  ]
}
