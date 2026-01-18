import {Component, inject, OnInit} from '@angular/core';
import {Breadcrumb} from 'primeng/breadcrumb';
import {RideModel} from '../../../model/ride-model';
import {ActivatedRoute} from '@angular/router';
import {RideService} from '../../../services/ride-service';

@Component({
  selector: 'app-your-ride-component',
  imports: [
    Breadcrumb,
  ],
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

  protected readonly rideService: RideService = inject(RideService);

  private route: ActivatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params =>
      this.rideService.findById(params["id"]).subscribe(ride => this.ride = ride)
    );
  }
}
