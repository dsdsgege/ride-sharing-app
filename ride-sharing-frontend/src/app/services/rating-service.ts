import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RatingResponseModel} from '../model/rating-response-model';

@Injectable({
  providedIn: 'root'
})
export class RatingService {

  private readonly url = "/api/rating";

  private readonly httpClient = inject(HttpClient);

  public getReceivedAsPassenger(page: number) {
    return this.httpClient.get<RatingResponseModel>(`${this.url}/received/passenger?page=${page}`);
  }

  public getReceivedAsDriver(page: number) {
    return this.httpClient.get<RatingResponseModel>(`${this.url}/received/driver?page=${page}`);
  }

  public getGivenAsPassenger(page: number) {
    return this.httpClient.get<RatingResponseModel>(`${this.url}/given/passenger?page=${page}`);
  }

  public getGivenAsDriver(page: number) {
    return this.httpClient.get<RatingResponseModel>(`${this.url}/given/driver?page=${page}`);
  }

  public addRatingAsDriver(driveId: number, username: string, rating: number, comment: string) {
    const model = {rated: {username: username}, value: rating, comment: comment};
    return this.httpClient.post<ResponseStatus>(`${this.url}/add/as-driver/${driveId}`, model);
  }

  public addRatingAsPassenger(rideId: number, username: string, rating: number, comment: string) {
    const model = {rated: {username: username}, value: rating, comment: comment};
    return this.httpClient.post<ResponseStatus>(`${this.url}/add/as-passenger/${rideId}`, model);
  }

  public getMyRatingCount() {
    return this.httpClient.get<number>(`${this.url}/my-count`);
  }
}
