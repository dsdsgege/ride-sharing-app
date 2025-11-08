import {Component} from '@angular/core';
import { InputText } from 'primeng/inputtext';
import { FloatLabel } from 'primeng/floatlabel';
import {FormsModule} from '@angular/forms';
import {DatePicker} from 'primeng/datepicker';
import {MapComponent} from '../map-component/map-component';
import {GeolocationService} from '@ng-web-apis/geolocation';
import {ProgressSpinner} from 'primeng/progressspinner';
import {take} from 'rxjs';
import {GeocodingService} from '../../services/geocoding-service';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {faLocationDot, faSearch} from '@fortawesome/free-solid-svg-icons';
import {Button} from 'primeng/button';

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
    Button
  ],
  templateUrl: './ride-component.html',
  styleUrl: './ride-component.scss'
})
export class RideComponent {

  protected date: Date = new Date();
  protected isGetLocation: boolean = false;
  protected position: GeolocationPosition | null = null;
  protected pickupValue: string = "";
  protected faLocationDot = faLocationDot;
  constructor(protected geolocation$: GeolocationService,
              private geocodingService: GeocodingService) {
  }

  getLocation() {
    this.isGetLocation = true;
    this.geolocation$.pipe(take(1)).subscribe((pos) => {
      this.position = pos;
      console.log(pos);
      this.geocodingService.getAddress(pos.coords.latitude, pos.coords.longitude).subscribe({
        next: res => {
          this.pickupValue = res[0].name;
        },
        error: () =>
          alert("Unexpected error happened while getting address")
      });
    });
  }

  protected readonly faSearch = faSearch;
}
