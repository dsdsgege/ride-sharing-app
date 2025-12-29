import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {Observable, of} from 'rxjs';
import { AddResponseModel } from '../model/add-response-model';

export interface PassengerPrice {
  price: number;
}

@Injectable({
  providedIn: 'root'
})
export class DriveService {
  private readonly apiUrl = "/api/drive";
  private readonly httpClient = inject(HttpClient);

  public getPrice(pickupFrom: string | null, dropOffTo: string | null, seats: number | null,
                  consumption: number | null, makeYear: number | null, carPrice: number | null
  ): Observable<PassengerPrice> {

    let params = new HttpParams()
      .set('pickup_from', pickupFrom ?? '')
      .set('drop_off_to', dropOffTo ?? '')
      .set('seats', seats?.toString() ?? '0')
      .set('consumption', consumption?.toString() ?? '0')
      .set('make_year', makeYear?.toString() ?? '')
      .set('car_price', carPrice?.toString() ?? '0');

    return this.httpClient.get<PassengerPrice>(`${this.apiUrl}/price`, {params});
  }

  public addDrive(driveData: any, passengerPrice: PassengerPrice | null): Observable<AddResponseModel> {
    if (!passengerPrice?.price) {
      let response = new AddResponseModel(false);
      return of(response);
    }

    driveData.dateFrom = driveData.dateFrom.toISOString();
    driveData.dateTo = driveData.dateTo.toISOString();

    driveData.passengerPrice = passengerPrice.price;
    return this.httpClient.post<AddResponseModel>(`${this.apiUrl}/add_drive`, driveData);
  }
}
