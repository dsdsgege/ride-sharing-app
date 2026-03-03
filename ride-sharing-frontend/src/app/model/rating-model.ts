import {UserModel} from './user-model';

export class RatingModel {
  constructor(public rater: UserModel,
              public rated: UserModel,
              public value: number,
              public comment: string){
  }
}
