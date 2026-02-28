import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {Observable, of} from 'rxjs';
import { AddResponseModel } from '../model/add-response-model';
import {DriverModel} from '../model/driver-model';
import {RideModelResponse} from '../model/ride/ride-model-response';
import {RideModel} from '../model/ride/ride-model';

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

    driveData.arrive = new Date(driveData.arrive).toISOString().split(".")[0];
    driveData.depart = new Date(driveData.depart).toISOString().split(".")[0];

    driveData.passengerPrice = passengerPrice.price;
    return this.httpClient.post<AddResponseModel>(`${this.apiUrl}/add_drive`, {drive: driveData, driver: driver});
  }

  public findDriveCountByUsername(username: String): Observable<number> {

    return this.httpClient.get<number>(`${this.apiUrl}/drive-count?username=${username}`);
  }

  public findDriverRatingByUsername(username: String): Observable<number> {

    return this.httpClient.get<number>(`${this.apiUrl}/driver-rating?username=${username}`);
  }

  public findMyDrives(page: number) {
    return this.httpClient.get<RideModelResponse>(`${this.apiUrl}/my-drives?page=${page}`);
  }

  public deleteDrive(driveId: number) {
    return this.httpClient.delete<ResponseStatus>(`${this.apiUrl}/my-drive/${driveId}`);
  }

  public updateDrive(rideModel: RideModel) {
    rideModel.depart = new Date(rideModel.depart).toISOString().split('.')[0];
    rideModel.arrive = new Date(rideModel.arrive).toISOString().split('.')[0];

    return this.httpClient.put<ResponseStatus>(`${this.apiUrl}/my-drive`, rideModel);
  }

  public acceptPassenger(token: String | undefined) {
    return this.httpClient.post<ResponseStatus>(`${this.apiUrl}/accept-passenger`, {token: token});
  }
}
