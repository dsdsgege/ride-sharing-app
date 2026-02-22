import {Component, inject, OnInit, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {RideModel} from '../../../model/ride/ride-model';
import {InputNumber} from 'primeng/inputnumber';
import {FormsModule} from '@angular/forms';
import {RideService} from '../../../services/ride-service';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-rides-tab-component',
  imports: [
    Button,
    InputNumber,
    FormsModule,
    DatePipe,
  ],
  templateUrl: './rides-tab-component.html',
  styleUrl: './rides-tab-component.scss'
})
export class RidesTabComponent implements OnInit {

  protected rides: RideModel[] = [];

  protected totalItems = 0;

  protected page = 0;

  protected selectedRide = signal<RideModel | undefined>(undefined);

  protected readonly rideService = inject(RideService);

  ngOnInit() {
    this.loadRides();
  }

  protected selectRide(ride: RideModel) {
    this.selectedRide.set(ride);
  }

  protected loadMore() {
    this.page++;
    this.loadRides();
  }

  private loadRides() {
    this.rideService.findMyRides(this.page).subscribe(response => {
      this.rides = response.content;
      this.totalItems = response.totalElements;
    });
  }
}
