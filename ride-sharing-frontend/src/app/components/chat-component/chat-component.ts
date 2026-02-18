import {Component, effect, inject, input, model, output} from '@angular/core';
import {faArrowCircleUp} from '@fortawesome/free-solid-svg-icons/faArrowCircleUp';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faXmark} from '@fortawesome/free-solid-svg-icons/faXmark';
import {faUser} from '@fortawesome/free-solid-svg-icons/faUser';
import {ChatService} from '../../services/chat-service';
import Keycloak from 'keycloak-js';
import {ChatModel} from '../../model/chat/ChatModel';
import {InputGroup} from 'primeng/inputgroup';
import {InputGroupAddon} from 'primeng/inputgroupaddon';
import {faPaperPlane} from '@fortawesome/free-solid-svg-icons';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-chat-component',
  standalone: true,
  imports: [
    FaIconComponent,
    InputGroup,
    InputGroupAddon,
    FormsModule
  ],
  templateUrl: './chat-component.html',
  styleUrl: './chat-component.scss'
})
export class ChatComponent {

  public otherName = input.required<string>();

  public otherUsername = input.required<string>();

  public isExpanded = model<boolean>(false);

  public close = output<void>();

  protected messages: ChatModel[] = [];

  protected currentMessage: string = "";

  protected readonly chatService = inject(ChatService);

  protected readonly keycloak = inject(Keycloak);

  protected readonly faArrowCircleUp = faArrowCircleUp;

  protected readonly faXmark = faXmark;

  protected readonly faUser = faUser;

  protected readonly faPaperPlane = faPaperPlane;

  constructor() {
    // IMPORTANT: It only starts the websocket connection when the chat is expanded!
    // listening to the changes of isExpaned so we can connect or disconnect from
    effect(() => {
      if (this.isExpanded()) {
        this.openMessaging();
      }
      if (!this.isExpanded()) {
        this.chatService.ngOnDestroy();
      }
    });
  }

  protected sendMessage() {

    this.keycloak.loadUserProfile().then(profile => {
      const currentMessageTemp = this.currentMessage;
      this.currentMessage = "";
      const message: ChatModel= {
        sender: profile.username!,
        receiver: this.otherUsername(),
        timestamp: Date.now(),
        message: currentMessageTemp
      }
      this.chatService.sendMessage(message)
    });
  }

  private openMessaging() {
    this.keycloak.loadUserProfile().then( async (profile) => {
      await this.chatService.openConnection(profile.username ?? "");
    }).catch( err => {
      this.keycloak.login();
      console.error(err);
    });

    this.chatService.getMessages().subscribe(msg => {
      if (msg) {
        this.messages.push(msg);
      }
    });
  }
}
