import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RideModelResponse} from '../model/ride-model-response';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private readonly apiUrl = environment.apiUrl;
  private readonly httpClient: HttpClient = inject(HttpClient);

  public findAll(page: number, pageSize: number, pickupFrom: string, dropOffTo: string,
                 dateFrom: Date, dateTo: Date): Observable<RideModelResponse> {

    return this.httpClient.get<RideModelResponse>(`${this.apiUrl}/rides?page=${page}&page_size=${pageSize}` +
      `&pickup_from=${pickupFrom}&drop_off_to=${dropOffTo}&date_from=${dateFrom}&date_to=${dateTo}`);
  }

  public getPrice(pickupFrom: string | null, dropOffTo: string | null,
                  seats: number | null, consumption: number | null) {

    return this.httpClient.get<number>(`${this.apiUrl}/rides/price?pickup_from=${pickupFrom}&drop_off_to=${dropOffTo}` +
    `&seats=${seats}&consumption=${consumption}`);
  }
}
