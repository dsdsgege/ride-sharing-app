import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavBar} from './components/nav-bar/nav-bar';
import {Footer} from './components/footer/footer';
import {Toast} from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBar, Footer, Toast],
  standalone: true,
  templateUrl: './app.html',
  providers: [MessageService],
  styleUrl: './app.scss'
})
export class App {
}
