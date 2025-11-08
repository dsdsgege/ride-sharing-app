import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {CarsService} from '../../services/cars-service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {faSearch} from '@fortawesome/free-solid-svg-icons';
import {DatePicker} from 'primeng/datepicker';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Listbox} from 'primeng/listbox';

@Component({
  selector: 'app-drive-component',
  imports: [
    ReactiveFormsModule,
    DatePicker,
    FaIconComponent,
    FloatLabel,
    InputText,
    Listbox
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

  protected carControl: FormControl = new FormControl('');

  protected readonly faSearch = faSearch;

  private readonly carsService: CarsService = inject(CarsService);

  constructor() {
    this.carMakes$ = this.carsService.fetchCarMakes();
  }

  ngOnInit(): void {

    this.carMakes$.subscribe({
      next: (response) => {
        this.carMakes = response.map(make => make.Make_Name)
          .filter(name => name.match(/^[A-za-z]\S*$/));
      },
      error: () => {
        alert("Unexpected error happened while fetching cars");
      }
    });

    this.carControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(search => this.searchCar(search));
  }

  private searchCar(search: string) {
    this.carMakesFiltered.set(this.carMakes.filter(
      make => make.toLowerCase().includes(search)
    ));
  }
}
