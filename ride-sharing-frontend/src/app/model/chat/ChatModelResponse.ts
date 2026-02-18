import {ChatModel} from './ChatModel';

export interface ChatModelResponse {
  content: ChatModel[];
  size: number;
  page: number;
  totalElements: number;
}
