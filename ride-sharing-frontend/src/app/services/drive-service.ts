import {inject, Injectable} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Observable} from 'rxjs';
import {AddResponseModel} from '../model/add-response-model';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DriveService {

  private readonly apiUrl = "/api";
  private readonly httpClient: HttpClient = inject(HttpClient);

  public getPrice(pickupFrom: string | null, dropOffTo: string | null, seats: number | null,
                  consumption: number | null, makeYear: number | null, carPrice: number | null) {

    return this.httpClient.get<PassengerPrice>(`${this.apiUrl}/drive/price?pickup_from=${pickupFrom}&` +
      `drop_off_to=${dropOffTo}&seats=${seats}&consumption=${consumption}&make_year=${makeYear}&car_price=${carPrice}`);
  }

  public addDrive(formGroup: FormGroup): Observable<AddResponseModel> {
    return this.httpClient.post<AddResponseModel>(`${this.apiUrl}/drive/add_drive`, formGroup);
  }
}

/**
 * This interface is used to return the price per passenger of the ride from the backend.
 */
export interface PassengerPrice {
  price: number;
}
