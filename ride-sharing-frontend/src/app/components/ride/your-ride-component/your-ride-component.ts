import {Component, inject, OnInit, signal} from '@angular/core';
import {Breadcrumb} from 'primeng/breadcrumb';
import {RideModel} from '../../../model/ride/ride-model';
import {ActivatedRoute} from '@angular/router';
import {RideService} from '../../../services/ride-service';
import {MessageService} from 'primeng/api';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faMessage} from '@fortawesome/free-solid-svg-icons/faMessage';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {ChatComponent} from '../../chat-component/chat-component';
import {Button} from 'primeng/button';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-your-ride-component',
  imports: [
    Breadcrumb,
    FaIconComponent,
    DatePipe,
    CurrencyPipe,
    ChatComponent,
    Button,
  ],
  providers: [MessageService],
  templateUrl: './your-ride-component.html',
  standalone: true,
  styleUrl: './your-ride-component.scss'
})
export class YourRideComponent implements OnInit {
  items = [
    {"label": "Search rides", routerLink: "/ride"},
    {"label": "Choose your ride", routerLink: "/ride/ride-list"},
    {"label": "Your ride", routerLink: "/ride/ride-list/your-ride"},
  ]

  protected ride!: RideModel;

  protected isChatVisible = signal<boolean>(true);

  protected isContactClicked = signal<boolean>(false);

  protected isChatExpanded = signal<boolean>(false);

  protected readonly rideService: RideService = inject(RideService);

  protected readonly faMessage = faMessage;

  private readonly route: ActivatedRoute = inject(ActivatedRoute);

  private readonly messageService: MessageService = inject(MessageService);

  private readonly keycloak = inject(Keycloak)

  ngOnInit(): void {
    this.route.queryParams.subscribe({
      next: params => {
        this.rideService.findById(params["id"]).subscribe(ride => this.ride = ride);
      },
      error: err => {
        console.error(err)
        this.messageService.add({severity: 'error', summary: 'Error', detail: "Your ride could not be found."});
      }
    });
  }

  protected closeChat() {
    this.isChatVisible.set(false);
  }

  protected contactClicked() {
    this.isChatVisible.set(true);
    this.isContactClicked.set(true);
    this.isChatExpanded.set(true);
  }

  protected joinRide() {
    this.keycloak.loadUserProfile().then(profile => {
      const fullName = profile.firstName + " " + profile.lastName;
      this.rideService.joinRide(this.ride.id, profile.username ?? "", profile.email ?? "", fullName).subscribe();
    }).catch(err => {
      this.keycloak.login();
      console.error(err);
    });
  }
}
