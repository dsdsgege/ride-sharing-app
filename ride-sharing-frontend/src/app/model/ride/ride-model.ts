import {DriverModel} from '../driver-model';

export interface RideModel {
  id: number;
  fromCity: string;
  toCity: string;
  price: number;
  driver: DriverModel;
  depart: Date | string;
  arrive: Date | string;
  carMake: string;
  seats: number;
}
