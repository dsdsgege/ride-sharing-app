import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GeocodingReverseResponseModel} from '../model/geocoding-reverse-response-model';

@Injectable({
  providedIn: 'root'
})

export class GeocodingService {
  private readonly apiUrl = "/api/geocoding";
  private readonly httpClient = inject(HttpClient);

  public getAddress(latitude: number, longitude: number): Observable<GeocodingReverseResponseModel[]> {
    // as our HTTP_INTERCEPTOR unwraps the response,
    // we return the array, not the first object
    return this.httpClient.get<GeocodingReverseResponseModel[]>(
      `${this.apiUrl}/address?latitude=${latitude}&longitude=${longitude}`
    );
  }
}
