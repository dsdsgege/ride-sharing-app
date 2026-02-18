import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProfileDataModel} from '../model/profile-data-model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  protected readonly httpClient = inject(HttpClient);

  protected readonly apiUrl = "/api/user";

  findUsersByUsernames(usernames: string[]) {
    return this.httpClient.post<UsersResponseModel>(`${this.apiUrl}/find/profiles`, {usernames: usernames});
  }
}

export interface UsersResponseModel {
  users: ProfileDataModel[];
}
