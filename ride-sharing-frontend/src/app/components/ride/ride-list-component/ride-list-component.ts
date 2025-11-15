import {Component, inject, OnChanges, OnInit} from '@angular/core';
import {Breadcrumb} from 'primeng/breadcrumb';
import {MenuItem} from 'primeng/api';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {RideService} from '../../../services/ride-service';
import {Observable} from 'rxjs';
import {RideModel} from '../../../model/ride-model';
import {RideModelResponse} from '../../../model/ride-model-response';
import {Card} from 'primeng/card';
import {DriverModel} from '../../../model/driver-model';
import {faUser} from '@fortawesome/free-solid-svg-icons/faUser';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {Button} from 'primeng/button';
import {Tooltip} from 'primeng/tooltip';

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
    RouterLink
  ],
  templateUrl: './ride-list-component.html',
  standalone: true,
  styleUrl: './ride-list-component.scss'
})
export class RideListComponent implements OnInit, OnChanges {

  protected page: number = 0;
  protected pageSize: number = 10;
  protected pickupFrom!: string;
  protected dropOffTo!: string;
  protected dateFrom!: Date;
  protected dateTo!: Date;

  driver1 = new DriverModel('d-001', 4.8);
  driver2 = new DriverModel('d-002', 4.5);
  driver3 = new DriverModel('d-003',  4.9);

  protected rides: RideModel[] = [
    new RideModel(
      this.driver2,
      'Debrecen, Vasútállomás',
      'Miskolc, Búza tér',
      new Date('2025-11-21T08:00:00'),
      new Date('2025-11-21T09:45:00'),
      2200),
    new RideModel(
      this.driver1,
      'Budapest, Népliget',
      'Szeged, Mars tér',
      new Date('2025-11-20T10:00:00'),
      new Date('2025-11-20T12:30:00'),
      3500),
    new RideModel(
      this.driver3,
      'Győr, Autóbusz-állomás',
      'Budapest, Kelenföld',
      new Date('2025-11-21T14:00:00'),
      new Date('2025-11-21T15:45:00'),
      2800
    ),
  ];

  protected readonly faUser = faUser;
  protected readonly dateFormat: string = "short" // M/d/yy, h:mm a

  private rideModelResponse$!: Observable<RideModelResponse>;

  private readonly rideService: RideService = inject(RideService)

  items: MenuItem[] = [
    {"label": "Search rides", routerLink: "/ride"},
    {"label": "Choose your ride", routerLink: "/ride/ride-list"},
  ]

  constructor(private readonly route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.pickupFrom = params["pickupFrom"];

      this.dropOffTo = params["dropOffTo"];

      this.dateFrom = new Date(params["date"]?.[0] ??
        JSON.parse(localStorage.getItem("date-range") ?? '[null, null]')[0]);

      this.dateTo = new Date(params["date"]?.[1] ??
        JSON.parse(localStorage.getItem("date-range") ?? '[null, null]')[1]);

    });

    console.log(this.dateTo, this.dateFrom)

    this.rideModelResponse$ = this.rideService.findAll(this.page, this.pageSize, this.pickupFrom, this.dropOffTo,
      this.dateFrom, this.dateTo);
  }

  ngOnChanges(): void {
    this.rideModelResponse$.subscribe(response => this.rides = response.content);
  }
}
