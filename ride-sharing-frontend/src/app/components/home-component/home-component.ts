import {Component} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-home-component',
  imports: [
    NgOptimizedImage,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    RouterLink
  ],
  templateUrl: './home-component.html',
  standalone: true,
  styleUrl: './home-component.scss'
})
export class HomeComponent {

  constructor() {
  }
}
