import { Injectable } from '@angular/core';
import {map, Observable} from 'rxjs';
import {CarMakeModel, CarMakeResponseModel} from '../model/car-make-response-model';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CarsService {
  apiUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/GetAllMakes?format=json";

  constructor(protected httpClient: HttpClient) {
  }
  public fetchCarMakes(): Observable<CarMakeModel[]> {
    console.log("cars fetched");
    return this.httpClient.get<CarMakeResponseModel>(this.apiUrl).pipe(
      map(response => {
        if (response?.Results) {
          return response.Results; // ...akkor adjuk vissza az autók tömbjét.
        }

        console.warn("API response was missing 'Results' property. Returning empty array.");
        return [];
      })
    );
  }
}
