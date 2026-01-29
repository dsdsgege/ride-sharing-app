export interface ChatModel {
  sender: string;
  receiver: string;
  timestamp: Date | null;
  message: string;
}
