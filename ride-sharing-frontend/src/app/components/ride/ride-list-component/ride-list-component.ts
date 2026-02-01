import {Component, inject, OnChanges, OnInit, signal} from '@angular/core';
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
    Toast
  ],
  providers: [MessageService],
  templateUrl: './ride-list-component.html',
  standalone: true,
  styleUrl: './ride-list-component.scss'
})
export class RideListComponent implements OnInit, OnChanges {

  protected page: number = 0;
  protected pageSize: number = 10;
  protected sortBy: string = "depart"
  protected direction: string = "asc";
  protected pickupFrom!: string;
  protected dropOffTo!: string;
  protected dateFrom!: Date;
  protected dateTo!: Date;

  protected rides: RideModel[] = [];

  protected noContent = signal(false)

  protected readonly faUser = faUser;
  protected readonly dateFormat: string = "short" // M/d/yy, h:mm a

  private rideModelResponse$!: Observable<RideModelResponse>;

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
    });

    console.log(this.dateTo, this.dateFrom)

    this.rideModelResponse$ = this.rideService.findAll(this.page, this.pageSize, this.sortBy, this.direction,
      this.pickupFrom, this.dropOffTo, this.dateFrom, this.dateTo);
    this.rideModelResponse$.subscribe(response => {
      this.rides = response.content;

      if (response.content.length === 0) {
        this.messageService.add({severity: 'warn', summary: 'No rides found', detail: 'No rides found for the given parameters.'});
        this.noContent.set(true);
        console.log("No rides found for the given parameters.")
      }
      console.log(response.content);
    });
  }

  ngOnChanges(): void {
    this.rideModelResponse$.subscribe(response => this.rides = response.content);
  }

  protected readonly faFilter = faFilter;
}
