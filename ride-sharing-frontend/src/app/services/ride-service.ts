import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RideModelResponse} from '../model/ride/ride-model-response';
import {RideModel} from '../model/ride/ride-model';
import {RideFilterModel} from '../model/ride/ride-filter-model';
import {FormService} from './form-service';

@Injectable({
  providedIn: 'root'
})
export class RideService {

  private readonly apiUrl = "/api/ride";

  private readonly httpClient: HttpClient = inject(HttpClient);

  private readonly formService = inject(FormService);

  public findAll(page: number, pageSize: number, sortBy: string, direction: string, pickupFrom: string,
                 dropOffTo: string, dateFrom: Date, dateTo: Date): Observable<RideModelResponse> {

    // Adjusting date as it will be converted to iso string
    dateFrom = this.formService.adjustDate(dateFrom) ?? new Date();
    dateTo = this.formService.adjustDate(dateTo) ?? new Date();
    dateTo.setHours(dateTo.getHours() + 23);
    dateTo.setMinutes(dateTo.getMinutes() + 59);

    return this.httpClient.get<RideModelResponse>(`${this.apiUrl}/rides?page=${page}&page_size=${pageSize}&` +
      `sort_by=${sortBy}&direction=${direction}&from=${pickupFrom}&to=${dropOffTo}&date_from=` +
      `${dateFrom.toISOString()}&date_to=${dateTo.toISOString()}`);
  }

  public findMyRides(page: number): Observable<RideModelResponse> {
    return this.httpClient.get<RideModelResponse>(`${this.apiUrl}/my-rides?page=${page}`);
  }

  public findByFilter(filter: RideFilterModel): Observable<RideModelResponse> {
    return this.httpClient.post<RideModelResponse>(`${this.apiUrl}/rides/filter`, filter);
  }

  public findById(id: number): Observable<RideModel> {
    return this.httpClient.get<RideModel>(`${this.apiUrl}/${id}`);
  }

  public getRideCountByUsername(username: String) {
    return this.httpClient.get<number>(`${this.apiUrl}/ride-count?username=${username}`);
  }

  public joinRide(id: number, username: string, email: string, fullName: string): Observable<ResponseStatus> {
    return this.httpClient.post<ResponseStatus>(`${this.apiUrl}/join/${id}`,
      {"username": username, "email": email, "fullName": fullName});
  }

  public cancelRide(id: number): Observable<ResponseStatus> {
    return this.httpClient.delete<ResponseStatus>(`${this.apiUrl}/leave/${id}`);
  }
}
