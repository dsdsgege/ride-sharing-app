import {Component, inject} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavBar} from './components/nav-bar/nav-bar';
import {Footer} from './components/footer/footer';
import {Toast} from 'primeng/toast';
import { MessageService } from 'primeng/api';
import {ProgressBar} from 'primeng/progressbar';
import {LoadingService} from './services/loading-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBar, Footer, Toast, ProgressBar],
  standalone: true,
  templateUrl: './app.html',
  providers: [MessageService],
  styleUrl: './app.scss'
})
export class App {
  protected readonly loadingService = inject(LoadingService);
}
