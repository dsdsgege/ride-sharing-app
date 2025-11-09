import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {CarsService} from '../../services/cars-service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {faSearch} from '@fortawesome/free-solid-svg-icons';
import {DatePicker} from 'primeng/datepicker';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Listbox, ListboxChangeEvent} from 'primeng/listbox';
import {InputNumber} from 'primeng/inputnumber';
import {FormService} from '../../services/form-service';

@Component({
  selector: 'app-drive-component',
  imports: [
    ReactiveFormsModule,
    DatePicker,
    FaIconComponent,
    FloatLabel,
    InputText,
    Listbox,
    InputNumber
  ],
  templateUrl: './drive-component.html',
  standalone: true,
  styleUrl: './drive-component.scss'
})
export class DriveComponent implements OnInit {

  protected carMakes$;

  protected carMakesFiltered: WritableSignal<string[]> = signal([]);

  protected showCarMakes = false;

  protected carMakes: string[] = [];

  protected carControl: FormControl<string | null> = new FormControl(null);

  protected consumptionControl: FormControl<number | null> = new FormControl(0);

  protected dateControl: FormControl<string | null> = new FormControl(null)

  protected readonly faSearch = faSearch;

  private readonly carsService: CarsService = inject(CarsService);

  private readonly formService: FormService = inject(FormService);

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

    this.formService.setValueFromLocalstorage('depart-date', this.dateControl);



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

  private searchCar(search: string) {
    this.carMakesFiltered.set(this.carMakes.filter(
      make => make.includes(search)
    ));
  }
}
