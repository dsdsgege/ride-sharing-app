import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RatingService {

  private readonly url = "/api/rating";

  private readonly httpClient = inject(HttpClient);

  public addRating(driveId: number, username: string, rating: number, comment: string) {
    return this.httpClient.post<ResponseStatus>(`${this.url}/add/${driveId}`,
      {rated: {username: username}, rating: rating, comment: comment}
    );
  }
}
