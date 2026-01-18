import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {Observable, of} from 'rxjs';
import { AddResponseModel } from '../model/add-response-model';
import {DriverModel} from '../model/driver-model';

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

  public addDrive(driveData: any, passengerPrice: PassengerPrice | null, driver: DriverModel):
    Observable<AddResponseModel> {

    if (!passengerPrice?.price) {
      let response = new AddResponseModel(false);
      return of(response);
    }

    console.log(driveData);

    driveData.arrive = new Date(driveData.arrive).toISOString();
    driveData.depart = new Date(driveData.depart).toISOString();

    driveData.passengerPrice = passengerPrice.price;
    return this.httpClient.post<AddResponseModel>(`${this.apiUrl}/add_drive`, {drive: driveData, driver: driver});
  }

  public findDriveCountByFullName(fullName: String): Observable<number> {
    console.log("findRideCountByPassenger called");
    return this.httpClient.get<number>(`${this.apiUrl}/drive-count?full_name=${fullName}`);
  }

  public findDriverRatingByFullName(fullName: String): Observable<number> {
    console.log("findRideCountByPassenger called");
    return this.httpClient.get<number>(`${this.apiUrl}/driver-rating?full_name=${fullName}`);
  }
}
