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
import {PassengerPrice, RideService} from '../../services/ride-service';
import {CurrencyPipe} from '@angular/common';
import { MessageService } from 'primeng/api';
import {Toast} from 'primeng/toast';

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
    Toast
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
  protected driveForm: FormGroup = new FormGroup({
    arrive: this.arriveControl,
    depart: this.departControl,
    modelYear: this.modelYearControl,
    seats: this.seatsControl,
    from: this.fromCityControl,
    to: this.toCityControl,
  });

  protected readonly today: Date = new Date();

  protected readonly dateFormat = 'dd/mm/yy';

  private readonly carsService: CarsService = inject(CarsService);

  private readonly rideService: RideService = inject(RideService);

  private readonly formService: FormService = inject(FormService);

  private readonly messageService = inject(MessageService);

  private everyInputFilled: boolean = false;

  constructor() {
    this.carMakes$ = this.carsService.fetchCarMakes();
  }

  ngOnInit(): void {
    this.carMakes$.subscribe({
      next: (response) => {
        this.carMakes = response.map(make => make.Make_Name)
          .filter(name => name.match(/^[A-Za-z]\S*$/));
      },
      error: () => {
        alert("Unexpected error happened while fetching cars");
      }
    });

    this.formService.setValueFromLocalstorage('car-make', this.carControl);
    this.formService.setValueFromLocalstorage('consumption', this.consumptionControl);
    this.formService.setValueFromLocalstorage('model-year', this.modelYearControl);
    this.formService.setValueFromLocalstorage('seats', this.seatsControl);
    this.formService.setValueFromLocalstorage('pickup-city-drive', this.fromCityControl);
    this.formService.setValueFromLocalstorage('dropoff-city-drive', this.toCityControl);
    this.formService.setValueFromLocastorageForDate('depart-date', this.departControl);
    this.formService.setValueFromLocastorageForDate('arrive-date', this.arriveControl);

    this.formService.setLocalStorageOnValueChanges('car-make', this.carControl);
    this.formService.setLocalStorageOnValueChanges('consumption', this.consumptionControl);
    this.formService.setLocalStorageOnValueChanges('model-year', this.modelYearControl);
    this.formService.setLocalStorageOnValueChanges('seats', this.seatsControl);
    this.formService.setLocalStorageOnValueChanges('pickup-city-drive', this.fromCityControl);
    this.formService.setLocalStorageOnValueChanges('dropoff-city-drive', this.toCityControl);
    this.formService.setLocalStorageForDateOnValueChanges('depart-date', this.departControl);
    this.formService.setLocalStorageForDateOnValueChanges('arrive-date', this.arriveControl);

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
    this.everyInputFilled = this.formService.areInputsFilled(this.carControl, this.consumptionControl,
      this.modelYearControl, this.seatsControl, this.fromCityControl, this.toCityControl, this.arriveControl,
      this.arriveControl, this.departControl);
    console.log(this.everyInputFilled);
    if (!this.everyInputFilled) {
      this.messageService.add({ severity: 'contrast', summary: 'Warning', detail: 'Please fill all the fields' });
      return;
    }
    this.rideService.getPrice(this.fromCityControl.value, this.toCityControl.value,
      this.seatsControl.value, this.consumptionControl.value).subscribe(
        price => {
          this.passengerPrice = price;
          this.dialogVisible = true;
        }
    );
  }

  protected addRide() {
    this.rideService.addRide(this.driveForm).subscribe({
      next: resp => {
        if (resp["success"]) {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Your ride is shared' })
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'We could not share your ride' })
        }
      },
      error: err =>  alert(err.message)
    });
  }

  //TODO:
  protected resetForm() {

  }

  private searchCar(search: string) {
    this.carMakesFiltered.set(this.carMakes.filter(
      make => make.toLowerCase().includes(search.toLowerCase())
    ));
  }
}
