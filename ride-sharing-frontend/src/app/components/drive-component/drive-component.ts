import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {CarsService} from '../../services/cars-service';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {DatePicker} from 'primeng/datepicker';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Listbox, ListboxChangeEvent} from 'primeng/listbox';
import {InputNumber} from 'primeng/inputnumber';
import {FormService} from '../../services/form-service';
import {Button} from 'primeng/button';
import {Dialog} from 'primeng/dialog';
import {CurrencyPipe} from '@angular/common';
import { MessageService } from 'primeng/api';
import {Toast} from 'primeng/toast';
import {PassengerPrice, DriveService} from '../../services/drive-service';
import Keycloak from 'keycloak-js';
import {DriverModel} from '../../model/driver-model';
import {ProgressBar} from 'primeng/progressbar';

@Component({
  selector: 'app-drive-component',
  imports: [
    ReactiveFormsModule,
    DatePicker,
    FloatLabel,
    InputText,
    Listbox,
    InputNumber,
    Button,
    Dialog,
    CurrencyPipe,
    Toast,
    ProgressBar
  ],
  templateUrl: './drive-component.html',
  standalone: true,
  providers: [MessageService],
  styleUrl: './drive-component.scss'
})
export class DriveComponent implements OnInit {

  protected carMakes$;

  protected carMakesFiltered: WritableSignal<string[]> = signal([]);

  protected showCarMakes: boolean = false;

  protected carMakes: string[] = [];

  protected dialogVisible: boolean = false;

  protected passengerPrice: PassengerPrice | null = null;

  protected carControl: FormControl<string | null> = new FormControl(null);
  protected consumptionControl: FormControl<number | null> = new FormControl(0);
  protected departControl: FormControl<Date | null> = new FormControl(null);
  protected arriveControl: FormControl<Date | null> = new FormControl(null);
  protected modelYearControl: FormControl<number | null> = new FormControl(null);
  protected seatsControl: FormControl<number | null> = new FormControl(null);
  protected fromCityControl: FormControl<string | null> = new FormControl(null);
  protected toCityControl: FormControl<string | null> = new FormControl(null);
  protected carPriceControl: FormControl<number | null> = new FormControl(null);

  protected driveForm: FormGroup = new FormGroup({
    arrive: this.arriveControl,
    depart: this.departControl,
    modelYear: this.modelYearControl,
    seats: this.seatsControl,
    fromCity: this.fromCityControl,
    toCity: this.toCityControl,
    carMake: this.carControl,
    carPrice: this.carPriceControl,
    consumption: this.consumptionControl
  });

  protected isLoading: WritableSignal<boolean> = signal(false);

  protected readonly today: Date = new Date();

  protected readonly dateFormat = 'dd/mm/yy';

  private readonly carsService: CarsService = inject(CarsService);

  private readonly driveService: DriveService = inject(DriveService);

  private readonly formService: FormService = inject(FormService);

  private readonly messageService = inject(MessageService);

  private readonly keycloak: Keycloak = inject(Keycloak);

  private everyInputFilled: boolean = false;

  private static readonly STORAGE_KEY = 'drive-form';

  constructor() {
    this.carMakes$ = this.carsService.fetchCarMakes();
  }

  ngOnInit(): void {
    this.carMakes$.subscribe({
      next: (response) => {
        this.carMakes = response.map(make => make.Make_Name)
          .filter(name => name.match(/^[A-Za-z]\S*$/));
      },
      error: () => this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not fetch cars' })
    });

    this.formService.loadForm(this.driveForm, DriveComponent.STORAGE_KEY);

    // subscribe to changes to save
    this.formService.persistForm(this.driveForm, DriveComponent.STORAGE_KEY);

    this.carControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(search => {
      this.searchCar(search ?? '');
    });
  }

  protected onCarMakesChange($event: ListboxChangeEvent) {
    this.carControl.setValue($event.value, { emitEvent: false });
    localStorage.setItem('car-make', $event.value);
    this.showCarMakes = false;
  }

  protected showDialog() {
    console.log('Keycloak state:', this.keycloak);
    console.log('Is authenticated:', this.keycloak.authenticated);

    this.everyInputFilled = this.formService.areInputsFilled(this.carControl, this.consumptionControl,
      this.modelYearControl, this.seatsControl, this.fromCityControl, this.toCityControl, this.arriveControl,
      this.departControl, this.carPriceControl
    );

    if (!this.everyInputFilled) {
      if (this.arriveControl.value && this.departControl.value && this.arriveControl.value < new Date()
        && this.departControl.value < new Date()) {

        this.messageService.add({ severity: 'contrast', summary: 'Warning', detail: 'Please select valid date' });
      } else {
        this.messageService.add({ severity: 'contrast', summary: 'Warning', detail: 'Please fill all the fields' });
      }
      return;
    }

    if (!this.keycloak.authenticated) {
      setTimeout(() => {
        console.log("Not authenticated, redirecting to login")
      }, 3000)
      this.keycloak.login({
        redirectUri: window.location.origin + window.location.pathname
      }).then(() => {
        this.isLoading.set(true);

        this.driveService.getPrice(this.fromCityControl.value, this.toCityControl.value, this.seatsControl.value,
          this.consumptionControl.value, this.modelYearControl.value, this.carPriceControl.value).subscribe(
          price => {
            this.passengerPrice = price;
            this.dialogVisible = true;
            this.isLoading.set(false);
          }
        );
      });
      return;
    }

    this.isLoading.set(true);
    this.driveService.getPrice(this.fromCityControl.value, this.toCityControl.value, this.seatsControl.value,
      this.consumptionControl.value, this.modelYearControl.value, this.carPriceControl.value).subscribe(
      price => {
        this.passengerPrice = price;
        this.dialogVisible = true;
        this.isLoading.set(false);
      }
    );
  }

  protected addDrive() {
    this.keycloak.loadUserProfile().then(profile => {
      let driver = new DriverModel(profile.firstName + " " + profile.lastName, 0);
      console.log(profile);
      console.log(driver);

      this.driveService.addDrive(this.driveForm.value, this.passengerPrice, driver).subscribe({
        next: resp => {
          const severity = resp.success ? 'success' : 'error';
          const detail = resp.success ? 'Your ride is shared' : 'Could not share ride';
          this.messageService.add({ severity, summary: resp.success ? 'Success' : 'Error', detail });
        },
        error: err => alert(err.message)
      });
    });
  }

  private searchCar(search: string) {
    this.carMakesFiltered.set(this.carMakes.filter(
      make => make.toLowerCase().includes(search.toLowerCase())
    ));
  }
}
