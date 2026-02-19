import {Component, inject, OnInit} from '@angular/core';
import { InputText } from 'primeng/inputtext';
import { FloatLabel } from 'primeng/floatlabel';
import { FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { DatePicker } from 'primeng/datepicker';
import { MapComponent } from '../../map-component/map-component';
import { GeolocationService } from '@ng-web-apis/geolocation';
import { ProgressSpinner } from 'primeng/progressspinner';
import { take } from 'rxjs';
import { GeocodingService } from '../../../services/geocoding-service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faLocationDot, faSearch } from '@fortawesome/free-solid-svg-icons';
import { Button } from 'primeng/button';
import { Tooltip } from 'primeng/tooltip';
import {FormService} from '../../../services/form-service';
import {Breadcrumb} from 'primeng/breadcrumb';
import {MenuItem} from 'primeng/api';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-ride-component',
  imports: [
    InputText,
    FloatLabel,
    FormsModule,
    DatePicker,
    MapComponent,
    ProgressSpinner,
    FontAwesomeModule,
    Button,
    ReactiveFormsModule,
    Tooltip,
    Breadcrumb,
    RouterLink],
  templateUrl: './ride-component.html',
  standalone: true,
  styleUrl: './ride-component.scss'
})
export class RideComponent implements OnInit {

  protected isGetLocation: boolean = false;

  protected dateControl: FormControl<Date[] | null> = new FormControl(null);
  protected pickupControl: FormControl<string | null> = new FormControl(null);
  protected dropoffControl: FormControl<string | null> = new FormControl(null);

  protected position: GeolocationPosition | null = null;

  protected readonly today = new Date();

  protected readonly faLocationDot = faLocationDot;

  protected readonly faSearch = faSearch;

  protected readonly Number = Number;

  protected readonly geolocation$: GeolocationService = inject(GeolocationService);

  private readonly geocodingService: GeocodingService = inject(GeocodingService);

  private readonly formService: FormService = inject(FormService);

  items: MenuItem[] = [
    {"label": "Search rides", routerLink: "/ride"},
    {"label": ""},
  ];

  constructor() {
  }

  ngOnInit(): void {

    this.formService.setLocalStorageOnValueChanges('pickup-city', this.pickupControl);
    this.formService.setLocalStorageOnValueChanges('dropoff-city', this.dropoffControl);

    this.dateControl.valueChanges.subscribe(
      value => {
        localStorage.setItem('date-range', JSON.stringify(value) ?? '');
      }
    );

    this.formService.setValueFromLocalstorage('pickup-city', this.pickupControl);
    this.formService.setValueFromLocalstorage('dropoff-city', this.dropoffControl);

    const storedDateString = localStorage.getItem('date-range');
    if (storedDateString) {
      const parsedValue = JSON.parse(storedDateString);

      if (Array.isArray(parsedValue)) {
        const dateObjects = parsedValue.map(dateStr => new Date(dateStr));
        // emitEvent: false stops from valuechanges emit
        this.dateControl.setValue(dateObjects, { emitEvent: false });
      }
    }

    if (localStorage.getItem('position')) {
      this.isGetLocation = true;
      this.position = JSON.parse(localStorage.getItem('position') ?? '');
    }
  }

  protected getLocation() {
    this.isGetLocation = true;
    this.geolocation$.pipe(take(1)).subscribe((pos) => {

      this.position = pos;
      localStorage.setItem('position', JSON.stringify(pos));

      this.geocodingService.getAddress(pos.coords.latitude, pos.coords.longitude).subscribe({
        next: res => {
          const pickupCity = res[0].name ?? '';
          this.pickupControl = new FormControl(pickupCity);
          localStorage.setItem('pickup-city', pickupCity);
        },
        error: err => alert('Unexpected error happened while getting address\n' + err.message)
      });
    });
  }
}
