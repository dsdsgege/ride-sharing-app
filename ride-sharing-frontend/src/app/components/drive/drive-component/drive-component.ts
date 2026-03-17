import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {CarsService} from '../../../services/cars-service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {catchError, debounceTime, distinctUntilChanged, filter, of, startWith, switchMap} from 'rxjs';
import {DatePicker} from 'primeng/datepicker';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Listbox, ListboxChangeEvent} from 'primeng/listbox';
import {InputNumber} from 'primeng/inputnumber';
import {FormService} from '../../../services/form-service';
import {Button} from 'primeng/button';
import {Dialog} from 'primeng/dialog';
import {SelectModule} from 'primeng/select';
import {CurrencyPipe} from '@angular/common';
import {MessageService} from 'primeng/api';
import {PassengerPrice, DriveService} from '../../../services/drive-service';
import Keycloak from 'keycloak-js';
import {UserModel} from '../../../model/user-model';
import {LoadingService} from '../../../services/loading-service';
import {finalize} from 'rxjs/operators';
import {CarModelModel} from '../../../model/car/car-model-model';
import {CarMakeModel} from '../../../model/car/car-make-model';
import {CarGenerationModel} from '../../../model/car/car-generation-model';
import {CarTrimModel} from '../../../model/car/car-trim-model';

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
    SelectModule,
    Dialog,
    CurrencyPipe,
    FormsModule
  ],
  templateUrl: './drive-component.html',
  standalone: true,
  styleUrl: './drive-component.scss'
})
export class DriveComponent implements OnInit {

  protected carMakesFiltered: WritableSignal<CarMakeModel[]> = signal([]);

  protected showCarMakes: boolean = false;

  protected dialogVisible: boolean = false;

  protected passengerPrice: PassengerPrice | null = null;

  protected carControl: FormControl<string | null> = new FormControl(null);
  protected departControl: FormControl<Date | null> = new FormControl(null);
  protected arriveControl: FormControl<Date | null> = new FormControl(null);
  protected seatsControl: FormControl<number | null> = new FormControl(null);
  protected fromCityControl: FormControl<string | null> = new FormControl(null);
  protected toCityControl: FormControl<string | null> = new FormControl(null);
  protected carPriceControl: FormControl<number | null> = new FormControl(null);
  protected modelControl: FormControl<CarModelModel | null> = new FormControl(null);
  protected generationControl: FormControl<CarGenerationModel | null> = new FormControl(null);
  protected trimControl: FormControl<CarTrimModel | null> = new FormControl(null);

  protected driveForm: FormGroup = new FormGroup({
    arrive: this.arriveControl,
    depart: this.departControl,
    model: this.modelControl,
    trim: this.trimControl,
    generation: this.generationControl,
    seats: this.seatsControl,
    fromCity: this.fromCityControl,
    toCity: this.toCityControl,
    carMake: this.carControl,
    carPrice: this.carPriceControl,
  });

  protected models: CarModelModel[] = [];

  protected generations: CarGenerationModel[] = [];

  protected trims: CarTrimModel[] = [];

  protected readonly today: Date = new Date();

  protected readonly dateFormat = 'dd/mm/yy';

  private everyInputFilled: boolean = false;

  private readonly loadingService = inject(LoadingService);

  private readonly carsService: CarsService = inject(CarsService);

  private readonly driveService: DriveService = inject(DriveService);

  private readonly formService: FormService = inject(FormService);

  private readonly messageService = inject(MessageService);

  private readonly keycloak: Keycloak = inject(Keycloak);

  private static readonly STORAGE_KEY = 'drive-form';

  ngOnInit(): void {
    this.formService.loadForm(this.driveForm, DriveComponent.STORAGE_KEY);

    // subscribe to changes to save
    this.formService.persistForm(this.driveForm, DriveComponent.STORAGE_KEY);

    this.carControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(search => {
      this.searchCar(search ?? '');
    });

    // --- Model to Generations ---
    this.modelControl.valueChanges.pipe(
      startWith(this.modelControl.value),
      // 1. Block empty values and 0
      filter(model => model !== null && model.id !== undefined && model.id !== 0),
      switchMap(model => this.carsService.getCarGenerations(model!.id).pipe(
        // 2. Catch errors so the stream doesn't die!
        catchError(() => of([]))
      ))
    ).subscribe(gens => {
      this.generations = gens;
    });

    // --- Generation to Trims ---
    this.generationControl.valueChanges.pipe(
      startWith(this.generationControl.value),
      // 1. Block empty values and 0
      filter(gen => gen !== null && gen.id !== undefined && gen.id !== 0),
      switchMap(gen => this.carsService.getCarTrim(gen!.id).pipe(
        // 2. Catch errors so the stream doesn't die!
        catchError(() => of([]))
      ))
    ).subscribe(trims => {
      this.trims = trims;
    });
  }

  protected onCarMakesChange($event: ListboxChangeEvent) {
    const carMake = $event.value as CarMakeModel;
    this.carControl.setValue(carMake.name, { emitEvent: false });
    localStorage.setItem('car-make', carMake.name);

    this.carsService.getCarModels(carMake.id).subscribe(response => this.models = response);

    this.showCarMakes = false;
  }

  protected showDialog() {
    this.everyInputFilled = this.formService.areInputsFilled(this.carControl, this.modelControl,
      this.seatsControl, this.fromCityControl, this.toCityControl, this.arriveControl, this.departControl,
      this.carPriceControl
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
      }, 3000)
      this.keycloak.login({
        redirectUri: window.location.origin + window.location.pathname
      }).then(() => {
        this.loadingService.show();

        console.log("TrimControl:" + JSON.stringify(this.trimControl));
        this.driveService.getPrice(this.fromCityControl.value, this.toCityControl.value, this.seatsControl.value,
          this.trimControl.value?.id ?? 0, this.carPriceControl.value).subscribe({
          next : price => {
            this.passengerPrice = price;
            this.dialogVisible = true;
          },
          error: err => {
            this.messageService.add({severity: 'error', summary: 'Error',
              detail: err.error.message ?? 'An error occurred.'});
            this.loadingService.hide();
            return;
          }
        });
      });
    }

    this.loadingService.show();
    this.driveService.getPrice(this.fromCityControl.value, this.toCityControl.value, this.seatsControl.value,
      this.trimControl.value?.id ?? 0, this.carPriceControl.value).pipe(finalize(
      () => this.loadingService.hide()
    )).subscribe(
      price => {
        this.passengerPrice = price;
        this.dialogVisible = true;
        this.loadingService.hide();
      }
    );
  }

  protected addDrive() {
    this.keycloak.loadUserProfile().then(profile => {
      let driver = new UserModel(profile.username ?? "", profile.firstName + " " + profile.lastName,
        profile.email ?? "", 0);


      this.driveService.addDrive(this.driveForm.value, this.passengerPrice, driver).subscribe({
        next: resp => {
          const severity = resp.success ? 'success' : 'error';
          const detail = resp.success ? 'Your ride is shared' : 'Could not share ride';
          this.messageService.add({ severity, summary: resp.success ? 'Success' : 'Error', detail});
          this.dialogVisible = false;
        },
        error: err => {
          this.messageService.add({severity: 'error', summary: 'Error',
            detail: err.error.message ?? 'An error occurred.'});
          this.loadingService.hide();
          return;
        }
      });
    });
  }

  private searchCar(search: string) {
    if (search.length < 2) {
      return;
    }

    const val = search.trim().toLowerCase();
    this.carsService.getCarMakes(val).subscribe(response => this.carMakesFiltered.set(response));
  }
}
