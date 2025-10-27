import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {GeocodingResponseModel} from '../model/geocoding-response-model';

@Injectable({
  providedIn: 'root'
})

export class GeocodingService {
  constructor(private httpClient: HttpClient) {
  }

  public getAddress(latitude: number, longitude: number): Observable<GeocodingResponseModel[]> {
    // as our HTTP_INTERCEPTOR unwraps the response,
    // we return the array, not the first object
    return this.httpClient.get<GeocodingResponseModel[]>(
      `http://api.openweathermap.org/geo/1.0/reverse?lat=${latitude}&lon=${longitude}&limit=1&appid=${environment.geolocation_api_key}`
    );
  }
}
