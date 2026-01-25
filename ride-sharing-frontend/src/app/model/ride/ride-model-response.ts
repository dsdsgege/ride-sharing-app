import {RideModel} from './ride-model';

export interface RideModelResponse {
  content: RideModel[];
  size: number;
  page: number;
  totalElements: number;
}
