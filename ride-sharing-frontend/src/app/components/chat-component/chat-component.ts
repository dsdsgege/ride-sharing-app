import {Component, inject, input, model, OnInit, output} from '@angular/core';
import {faArrowCircleUp} from '@fortawesome/free-solid-svg-icons/faArrowCircleUp';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faXmark} from '@fortawesome/free-solid-svg-icons/faXmark';
import {faUser} from '@fortawesome/free-solid-svg-icons/faUser';
import {ChatService} from '../../services/chat-service';
import Keycloak from 'keycloak-js';
import {ChatModel} from '../../model/chat/ChatModel';

@Component({
  selector: 'app-message-component',
  standalone: true,
  imports: [
    FaIconComponent
  ],
  templateUrl: './chat-component.html',
  styleUrl: './chat-component.scss'
})
export class ChatComponent implements OnInit{

  public name = input.required<string>();

  public isExpanded = model<boolean>(false);

  public close = output<void>();

  protected readonly faArrowCircleUp = faArrowCircleUp;
  protected readonly faXmark = faXmark;
  protected readonly faUser = faUser;

  protected readonly chatService = inject(ChatService);

  protected readonly keycloak = inject(Keycloak);

  public messages: ChatModel[] = [];

  ngOnInit(): void {
    this.keycloak.loadUserProfile().then(profile => {
      this.chatService.openConnection(profile.firstName + " " + profile.lastName);

      this.chatService.connectedSubj.subscribe(isConnected => {
        if (isConnected) {
          const message = {
            sender: profile.firstName + " " + profile.lastName,
            receiver: "System",
            timestamp: null,
            message: "You have successfully connected to the chat."
          };
          this.chatService.sendMessage(message)
        }
      })
    });


    this.chatService.getMessages().subscribe(msg => {
      if (msg) {
        this.messages.push({
          message: msg.message,
          sender: msg.sender,
          receiver: msg.receiver,
          timestamp: new Date()
        });
      }
    });
  }
}
