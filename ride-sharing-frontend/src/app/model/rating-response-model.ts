import {RatingModel} from './rating-model';

export interface RatingResponseModel {
  content: RatingModel[];
  totalElements: number;
}
