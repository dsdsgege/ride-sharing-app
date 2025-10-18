import {Component} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';

@Component({
  selector: 'app-home-component',
  imports: [
    NgOptimizedImage,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent],
  templateUrl: './home-component.html',
  styleUrl: './home-component.scss'
})
export class HomeComponent {

  constructor() {
  }
}
