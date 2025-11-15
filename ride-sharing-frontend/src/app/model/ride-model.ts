import {DriverModel} from './driver-model';

export class RideModel {
  constructor(public driver: DriverModel,
              public departFrom: string,
              public arriveTo: string,
              public departDate: Date,
              public arriveDate: Date,
              public passengerPrice: number) {
  }
}
