import {Component, inject, OnInit} from '@angular/core';
import { InputText } from 'primeng/inputtext';
import { FloatLabel } from 'primeng/floatlabel';
import { FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { DatePicker } from 'primeng/datepicker';
import { MapComponent } from '../map-component/map-component';
import { GeolocationService } from '@ng-web-apis/geolocation';
import { ProgressSpinner } from 'primeng/progressspinner';
import { take } from 'rxjs';
import { GeocodingService } from '../../services/geocoding-service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faLocationDot, faSearch } from '@fortawesome/free-solid-svg-icons';
import { Button } from 'primeng/button';
import { Tooltip } from 'primeng/tooltip';

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
    Tooltip
  ],
  templateUrl: './ride-component.html',
  standalone: true,
  styleUrl: './ride-component.scss'
})
export class RideComponent implements OnInit {

  protected isGetLocation: boolean = false;

  protected dateControl: FormControl<Date[] | null> = new FormControl(null);

  protected pickupControl: FormControl<string | null> =
    new FormControl(localStorage.getItem('pickup-city') ?? '');

  protected dropoffControl: FormControl<string | null> =
    new FormControl(localStorage.getItem('dropoff-city') ?? '');

  protected readonly faLocationDot = faLocationDot;

  protected readonly faSearch = faSearch;

  protected position: GeolocationPosition | null = null;

  protected readonly Number = Number;

  protected readonly geolocation$: GeolocationService = inject(GeolocationService);

  private readonly geocodingService: GeocodingService = inject(GeocodingService);

  constructor() {
  }

  ngOnInit(): void {
    this.pickupControl.valueChanges.subscribe(
      value => localStorage.setItem('pickup-city', value ?? '')
    );

    this.dropoffControl.valueChanges.subscribe(
      value => localStorage.setItem('dropoff-city', value ?? '')
    );

    const storedDateString = localStorage.getItem('date-range');
    if (storedDateString) {
      const parsedValue = JSON.parse(storedDateString);

      if (Array.isArray(parsedValue)) {
        const dateObjects = parsedValue.map(dateStr => new Date(dateStr));
        this.dateControl.setValue(dateObjects);
      }
    }

    this.dateControl.valueChanges.subscribe(
      value => {
        localStorage.setItem('date-range', JSON.stringify(value) ?? '');
      }
    )

    if (localStorage.getItem('position')) {
      this.isGetLocation = true;
      this.position = JSON.parse(localStorage.getItem('position') ?? '');
    }
  }

  getLocation() {
    this.isGetLocation = true;
    this.geolocation$.pipe(take(1)).subscribe((pos) => {

      this.position = pos;
      localStorage.setItem('position', JSON.stringify(pos));

      this.geocodingService.getAddress(pos.coords.latitude, pos.coords.longitude).subscribe({
        next: res => this.pickupControl = new FormControl(res[0].name ?? ''),
        error: err => alert('Unexpected error happened while getting address\n' + err.message)
      });
    });
  }
}
