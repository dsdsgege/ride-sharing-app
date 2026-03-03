import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RatingResponseModel} from '../model/rating-response-model';
import {RatingModel} from '../model/rating-model';

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

  public addRating(driveId: number, username: string, rating: number, comment: string) {
    const model = {rated: {username: username}, value: rating, comment: comment};
    return this.httpClient.post<ResponseStatus>(`${this.url}/add/${driveId}`, model);
  }
}
