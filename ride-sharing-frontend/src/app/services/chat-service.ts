import {Injectable, OnDestroy} from '@angular/core';
import {ChatModel} from '../model/chat/ChatModel';
import {BehaviorSubject, Observable} from 'rxjs';
import {Client, Message} from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService implements OnDestroy {

  public connectedSubj = new BehaviorSubject<boolean>(false)

  private stompClient: Client | null = null;

  private messageSubject = new BehaviorSubject<ChatModel | null>(null);

  private readonly protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';

  private readonly host = window.location.host;

  private readonly url = `${this.protocol}${this.host}/ws`;

  constructor() {
  }

  openConnection(fullName: string){
    this.stompClient = new Client({
      brokerURL: this.url,

      debug: (str) => console.log(str),
      reconnectDelay: 5000, // Auto-reconnect
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected: ' + this.stompClient?.connected);

      // The client only subscribes to their own topic as backend sends the message to both topic
      this.stompClient?.subscribe(`/topic/private-message/${fullName}`, (message: Message) => {
        if (message.body) {
          const parsedMessage: ChatModel = JSON.parse(message.body);
          this.messageSubject.next(parsedMessage);
        }
      });

      this.connectedSubj.next(true);
    };

    this.stompClient.activate();
  }

  sendMessage(message: ChatModel) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: `/app/chat/private-message`,
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message. Socket is not connected.')
    }
  }

  getMessages(): Observable<ChatModel | null> {
    return this.messageSubject.asObservable();
  }

  ngOnDestroy() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }
}
