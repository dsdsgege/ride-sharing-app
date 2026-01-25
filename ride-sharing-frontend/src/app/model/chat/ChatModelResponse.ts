import {ChatModel} from './ChatModel';

export interface RideModelResponse {
  content: ChatModel[];
  size: number;
  page: number;
  totalElements: number;
}
