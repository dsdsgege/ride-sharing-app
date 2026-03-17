import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CarMakeModel} from '../model/car/car-make-model';
import {HttpClient} from '@angular/common/http';
import {CarGenerationModel} from '../model/car/car-generation-model';
import {CarTrimModel} from '../model/car/car-trim-model';

@Injectable({
  providedIn: 'root'
})
export class CarsService {

  private readonly apiUrl = "/api/car";

  private readonly httpClient = inject(HttpClient);

  public getCarMakes(make: string): Observable<CarMakeModel[]> {
    return this.httpClient.get<CarMakeModel[]>(this.apiUrl + `/make?car_make=${make}`);
  }

  public getCarModels(makeId: number): Observable<CarMakeModel[]> {
    return this.httpClient.get<CarMakeModel[]>(this.apiUrl + `/model?make_id=${makeId}`);
  }

  public getCarGenerations(modelId: number): Observable<CarGenerationModel[]> {
    return this.httpClient.get<[CarGenerationModel]>(this.apiUrl + `/generation?model_id=${modelId}`);
  }

  public getCarTrim(generationId: number): Observable<CarTrimModel[]> {
    return this.httpClient.get<CarTrimModel[]>(this.apiUrl + `/trim?generation_id=${generationId}`);
  }
}
