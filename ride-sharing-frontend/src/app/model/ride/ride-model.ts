import {UserModel} from '../user-model';

export interface RideModel {
  id: number;
  fromCity: string;
  toCity: string;
  price: number;
  driver: UserModel;
  depart: Date | string;
  arrive: Date | string;
  carMake: string;
  seats: number;
}
