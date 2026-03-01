import {RideModel} from './ride-model';
import {UserModel} from '../user-model';

export interface RideModelWithPassengers extends RideModel {
  passengersToRate: [UserModel]
}
