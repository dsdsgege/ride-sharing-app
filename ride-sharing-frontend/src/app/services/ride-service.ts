import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RideModelResponse} from '../model/ride/ride-model-response';
import {RideModel} from '../model/ride/ride-model';

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private readonly apiUrl = "/api/ride";
  private readonly httpClient: HttpClient = inject(HttpClient);

  public findAll(page: number, pageSize: number, sortBy: string, direction: string, pickupFrom: string,
                 dropOffTo: string, dateFrom: Date, dateTo: Date): Observable<RideModelResponse> {

    console.log("findAll");
    return this.httpClient.get<RideModelResponse>(`${this.apiUrl}/rides?page=${page}&page_size=${pageSize}&` +
      `sort_by=${sortBy}&direction=${direction}&from=${pickupFrom}&to=${dropOffTo}&date_from=` +
      `${dateFrom.toISOString()}&date_to=${dateTo.toISOString()}`);
  }

  public findById(id: number): Observable<RideModel> {
    return this.httpClient.get<RideModel>(`${this.apiUrl}/${id}`);
  }

  public findRideCountByUsername(username: String) {
    console.log("findRideCountByPassenger called");
    return this.httpClient.get<number>(`${this.apiUrl}/ride-count?username=${username}`);
  }

  public joinRide(id: number, username: string, email: string, fullName: string): Observable<RideModel> {
    return this.httpClient.post<RideModel>(`${this.apiUrl}/join/${id}`,
      {"username": username, "email": email, "fullName": fullName});
  }
}
