import {DriverModel} from '../driver-model';

export interface RideModel {
  id: number;
  fromCity: string;
  toCity: string;
  price: number;
  driver: DriverModel;
  departureTime: Date | string;
  arrivalTime: Date | string;
  carMake: string;
  seats: number;
}
