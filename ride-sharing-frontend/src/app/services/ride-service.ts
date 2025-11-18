import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RideModelResponse} from '../model/ride-model-response';
import {FormGroup} from '@angular/forms';
import {AddResponseModel} from '../model/add-response-model';

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private readonly apiUrl = "/api";
  private readonly httpClient: HttpClient = inject(HttpClient);

  public findAll(page: number, pageSize: number, pickupFrom: string, dropOffTo: string,
                 dateFrom: Date, dateTo: Date): Observable<RideModelResponse> {

    return this.httpClient.get<RideModelResponse>(`${this.apiUrl}/rides?page=${page}&page_size=${pageSize}` +
      `&pickup_from=${pickupFrom}&drop_off_to=${dropOffTo}&date_from=${dateFrom}&date_to=${dateTo}`);
  }

  public getPrice(pickupFrom: string | null, dropOffTo: string | null,
                  seats: number | null, consumption: number | null) {

    return this.httpClient.get<PassengerPrice>(`${this.apiUrl}/rides/price?pickup_from=${pickupFrom}&drop_off_to=${dropOffTo}` +
      `&seats=${seats}&consumption=${consumption}`);
  }

  public addRide(formGroup: FormGroup): Observable<AddResponseModel> {
    return this.httpClient.post<AddResponseModel>(`${this.apiUrl}/rides/add_ride`, formGroup);
  }
}

export interface PassengerPrice {
  price: number;
}
//TODO: THIS IS DRIVE-SERVICE (SOME OF IT!!!)
