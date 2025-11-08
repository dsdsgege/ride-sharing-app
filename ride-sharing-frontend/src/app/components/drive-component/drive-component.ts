import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {CarsService} from '../../services/cars-service';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-drive-component',
  imports: [],
  templateUrl: './drive-component.html',
  standalone: true,
  styleUrl: './drive-component.scss'
})
export class DriveComponent implements OnInit {

  protected carMakes$;
  protected carMakesFiltered: WritableSignal<string[]> = signal([]);
  protected carMakes: string[] = [];
  protected carControl: FormControl = new FormControl('');

  private readonly carsService: CarsService = inject(CarsService);

  constructor() {
    this.carMakes$ = this.carsService.fetchCarMakes();
  }

  ngOnInit(): void {
     this.carMakes$.subscribe({
      next: (response) => {
        console.log("subscription happened");
        console.log("response: " + response);
        this.carMakes = response.map(make => make.Make_Name);
        console.log("carMakes: " + this.carMakes);
      },
      error: () => {
        alert("Unexpected error happened while fetching cars");
      }
    });
  }

  protected searchCar(event: Event) {
    const input = event?.target as HTMLInputElement;
    const value = input?.value;

    this.carMakesFiltered.set(this.carMakes.filter(
      make => make.toLowerCase().includes(value)
    ));
  }
}
