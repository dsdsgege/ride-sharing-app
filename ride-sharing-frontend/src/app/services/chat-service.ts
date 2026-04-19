import {inject, Injectable, OnDestroy} from '@angular/core';
import {ChatModel} from '../model/chat/ChatModel';
import {BehaviorSubject, Observable} from 'rxjs';
import {Client, Message} from '@stomp/stompjs';
import {HttpClient} from '@angular/common/http';
import {ChatModelResponse} from '../model/chat/ChatModelResponse';

@Injectable({
  providedIn: 'root'
})
export class ChatService implements OnDestroy {

  public connectedSubj = new BehaviorSubject<boolean>(false)

  private stompClient: Client | null = null;

  private messageSubject = new BehaviorSubject<ChatModel | null>(null);

  private readonly webSocketProtocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';

  private readonly host = window.location.host;

  private readonly webSocketUrl = `${this.webSocketProtocol}${this.host}/ws`;

  private readonly httpClient = inject(HttpClient);

  constructor() {
  }

  ngOnDestroy() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  // STOMP/WEBSOCKET
  openConnection(roomName: string): Promise<void> {

    // do not open a new connection if already connected
    if (this.stompClient && this.stompClient.connected) {
      return Promise.resolve();
    }

    this.stompClient = new Client({
      brokerURL: this.webSocketUrl,
      reconnectDelay: 5000, // Auto-reconnect
    });

    const client = this.stompClient;
    return new Promise(resolve => {

      // defining the callback on connection
      client.onConnect = (frame) => {

        // The client only subscribes to their own topic as backend sends the message to both topic
        this.stompClient?.subscribe(`/topic/private-messages/${roomName}`, (message: Message) => {
          if (message.body) {
            const parsedMessage: ChatModel = JSON.parse(message.body);
            this.messageSubject.next(parsedMessage);
          }
        });

        this.connectedSubj.next(true);
        resolve();
      };

      client.activate();
    });
  }

  closeConnection() {
    if (this.stompClient && this.stompClient.connected) {
      void this.stompClient.deactivate();
    }
  }

  sendMessage(message: ChatModel) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: `/app/chat/private-messages`,
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message. Socket is not connected.')
    }
  }

  getMessages(): Observable<ChatModel | null> {
    return this.messageSubject.asObservable();
  }

  // REST
  getChatPartnersByUsername(username: string, page: number) {
    return this.httpClient.get<ChatPartnerResponse>(`/api/chat/partners?username=${username}&page=${page}`);
  }

  getChatHistoryBetweenUsers(partnerUsername: string, page: number) {
    return this.httpClient.get<ChatModelResponse>(
      `/api/chat/history?partner=${partnerUsername}&page=${page}`
    );
  }

  getMyPartnerCount() {
    return this.httpClient.get<number>(`/api/chat/partner-count`);
  }
}

interface ChatPartnerResponse {
  content: string[];
  totalElements: number;
}
