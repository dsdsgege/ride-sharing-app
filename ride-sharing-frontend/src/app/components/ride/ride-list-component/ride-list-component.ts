import {Component, inject, OnInit, signal} from '@angular/core';
import {Breadcrumb} from 'primeng/breadcrumb';
import {MenuItem, MessageService} from 'primeng/api';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {RideService} from '../../../services/ride-service';
import {Observable} from 'rxjs';
import {RideModel} from '../../../model/ride/ride-model';
import {RideModelResponse} from '../../../model/ride/ride-model-response';
import {Card} from 'primeng/card';
import { ProgressBarModule } from 'primeng/progressbar';
import {faUser} from '@fortawesome/free-solid-svg-icons/faUser';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {Button} from 'primeng/button';
import {Tooltip} from 'primeng/tooltip';
import {Toast} from 'primeng/toast';
import {faFilter} from '@fortawesome/free-solid-svg-icons';
import {Dialog} from 'primeng/dialog';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {DatePicker} from 'primeng/datepicker';
import {InputNumber} from 'primeng/inputnumber';
import {RideFilterModel} from '../../../model/ride/RideFilterModel';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';

@Component({
  selector: 'app-ride-list',
  imports: [
    Breadcrumb,
    Card,
    DatePipe,
    CurrencyPipe,
    FaIconComponent,
    Button,
    Tooltip,
    RouterLink,
    ProgressBarModule,
    Toast,
    Dialog,
    FloatLabel,
    InputText,
    ReactiveFormsModule,
    DatePicker,
    InputNumber,
    CheckboxModule,
    FormsModule
  ],
  providers: [MessageService],
  templateUrl: './ride-list-component.html',
  standalone: true,
  styleUrl: './ride-list-component.scss'
})
export class RideListComponent implements OnInit {

  protected page: number = 0;
  protected pageSize: number = 10;
  protected sortBy: string = "depart"
  protected direction: string = "asc";
  protected pickupFrom!: string;
  protected dropOffTo!: string;
  protected dateFrom!: Date;
  protected dateTo!: Date;

  // Filter form controls
  protected dateControl: FormControl<Date[] | null> = new FormControl(null);
  protected pickupControl: FormControl<string | null> = new FormControl(null);
  protected dropoffControl: FormControl<string | null> = new FormControl(null);
  protected seatsControl: FormControl<number | null> = new FormControl(1);
  protected passengerPriceControl: FormControl<number | null> = new FormControl(null);
  protected ratingControl: FormControl<number | null> = new FormControl(1);
  protected ratingCheck = true;
  protected withoutRatingControl: FormControl<boolean | null> = new FormControl(true);

  protected rides: RideModel[] = [];

  protected loading = signal(false)

  protected filterDialogVisible: boolean = false;

  protected dialogWidth = '50%';

  protected readonly faUser = faUser;
  protected readonly dateFormat: string = "short" // M/d/yy, h:mm a
  protected readonly faFilter = faFilter;

  private readonly rideService: RideService = inject(RideService)

  private readonly messageService = inject(MessageService);

  items: MenuItem[] = [
    {"label": "Search rides", routerLink: "/ride"},
    {"label": "Choose your ride", routerLink: "/ride/ride-list"},
  ]

  constructor(private readonly route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.pickupFrom = params["pickupFrom"] ?? localStorage.getItem("pickup-city") ?? '';

      this.dropOffTo = params["dropOffTo"] ?? localStorage.getItem("dropoff-city") ?? '';

      this.dateFrom = new Date(params["date"]?.[0] ??
        JSON.parse(localStorage.getItem("date-range") ?? '[null, null]')[0]);

      this.dateTo = new Date(params["date"]?.[1] ??
        JSON.parse(localStorage.getItem("date-range") ?? '[null, null]')[1]);

      // setting from basic filter
      this.pickupControl.setValue(this.pickupFrom);
      this.dropoffControl.setValue(this.dropOffTo);
      this.dateControl.setValue([this.dateFrom, this.dateTo]);
    });

    console.log(this.dateTo, this.dateFrom)

    this.loading.set(true);
    this.rideService.findAll(this.page, this.pageSize, this.sortBy, this.direction,
      this.pickupFrom, this.dropOffTo, this.dateFrom, this.dateTo).subscribe(response => {
        this.rides = response.content;

        if (response.content.length === 0) {
          this.messageService.add({severity: 'warn', summary: 'No rides found',
            detail: 'No rides found for the given parameters.'});
          console.log("No rides found for the given parameters.")
        }
        console.log(response.content);
        this.loading.set(false);
      });

    // load saved advanced filter to inputs
    let filter = JSON.parse(localStorage.getItem("ride-filter") ?? "null");
    if (filter) {
      this.pickupControl.setValue(filter.pickupFrom);
      this.dropoffControl.setValue(filter.dropOffTo);
      this.dateControl.setValue([new Date(filter.dateFrom), new Date(filter.dateTo)]);
      this.seatsControl.setValue(filter.seats);
      this.passengerPriceControl.setValue(filter.price);
      this.ratingControl.setValue(filter.rating);
    }
  }

  filterRides() {
    this.filterDialogVisible = false;
    // save the changes in the basic filter
    if (this.dateControl.value) {
      this.dateFrom = this.dateControl.value[0];
      this.dateTo = this.dateControl.value[1];
      localStorage.setItem("date-range", JSON.stringify([this.dateFrom, this.dateTo]));
    }

    if (this.pickupControl.value) {
      this.pickupFrom = this.pickupControl.value;
      localStorage.setItem("pickup-city", this.pickupControl.value);
    }

    if (this.dropoffControl.value) {
      this.dropOffTo = this.dropoffControl.value;
      localStorage.setItem("dropoff-city", this.dropoffControl.value);
    }

    // apply advanced filter
    let filter: RideFilterModel = {
      pickupFrom: this.pickupControl.value,
      dropOffTo: this.dropoffControl.value,
      dateFrom: this.dateControl.value?.[0],
      dateTo: this.dateControl.value?.[1],
      seats: this.seatsControl.value,
      maxPrice: this.passengerPriceControl.value,
      rating: this.ratingControl.value,
      showWithoutRating: this.withoutRatingControl.value,
      page: this.page,
      pageSize: this.pageSize,
      sortBy: this.sortBy,
      direction: this.direction,
    }
    localStorage.setItem("ride-filter", JSON.stringify(filter));

    this.loading.set(true);

    this.rideService.findByFilter(filter).subscribe(response => {
      this.rides = response.content;
      if (response.content.length === 0) {
        this.messageService.add({severity: 'warn', summary: 'No rides found',
          detail: 'No rides found for the given parameters.'});
      }
      this.loading.set(false);
    });
  }
}
