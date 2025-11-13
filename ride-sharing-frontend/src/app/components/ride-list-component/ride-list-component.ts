import { Component } from '@angular/core';
import {Breadcrumb} from 'primeng/breadcrumb';
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-ride-list',
  imports: [
    Breadcrumb
  ],
  templateUrl: './ride-list-component.html',
  styleUrl: './ride-list-component.scss'
})
export class RideListComponent {
  items: MenuItem[] = [
    {"label": "Find your ride", routerLink: "/ride"},
    {"label": "Your ride", routerLink: "/ride-list"},
  ]
}
