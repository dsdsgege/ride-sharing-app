import {
  AfterViewInit,
  Component,
  effect,
  ElementRef,
  inject,
  input,
  model,
  output,
  ViewChild
} from '@angular/core';
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
import {Button} from 'primeng/button';

@Component({
  selector: 'app-chat-component',
  standalone: true,
  imports: [
    FaIconComponent,
    InputGroup,
    InputGroupAddon,
    FormsModule,
    Button
  ],
  templateUrl: './chat-component.html',
  styleUrl: './chat-component.scss'
})
export class ChatComponent implements AfterViewInit {

  public otherName = input.required<string>();

  public otherUsername = input.required<string>();

  public backgroundTransparent = input<boolean>(false);

  public isExpanded = model<boolean>(false);

  public close = output<void>();

  protected messages: ChatModel[] = [];

  protected currentMessage: string = "";

  protected totalMessages = 0;

  protected page = 0;

  protected readonly chatService = inject(ChatService);

  protected readonly keycloak = inject(Keycloak);

  protected readonly faArrowCircleUp = faArrowCircleUp;

  protected readonly faXmark = faXmark;

  protected readonly faUser = faUser;

  protected readonly faPaperPlane = faPaperPlane;

  @ViewChild("messagesContainer") private readonly scrollBar!: ElementRef;

  private username: string = "";

  constructor() {
    // IMPORTANT: It only starts the websocket connection when the chat is expanded!
    // listening to the changes of isExpaned so we can connect or disconnect from the websocket
    effect(() => {
      if (this.isExpanded()) {
        this.openMessaging();
      }
      if (!this.isExpanded()) {
        this.chatService.ngOnDestroy();
      }
      if (this.otherUsername()) {
        this.loadHistory();
      }
    });
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  // Track user's scroll position
  protected onScroll(): void {
    const scrollBar = this.scrollBar.nativeElement;
    const threshold = 10;
    const position = scrollBar.scrollTop + scrollBar.clientHeight;
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
      this.chatService.sendMessage(message);
      this.scrollToBottom();
    });
  }

  protected async loadMore() {
    this.page++;
    const scrollBar = this.scrollBar.nativeElement;

    const previousHeight = scrollBar.scrollHeight;

    await this.loadHistory();

    setTimeout(() => {
      const newScrollHeight = scrollBar.scrollHeight;
      const heightDifference = newScrollHeight - previousHeight;

      scrollBar.scrollTop = scrollBar.scrollTop + heightDifference;
    }, 10);
  }

  private openMessaging() {
    this.keycloak.loadUserProfile().then( async (profile) => {
      this.username = profile.username ?? "";
      await this.loadHistory();
      await this.chatService.openConnection(this.username);
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

  private loadHistory() {
    return new Promise<void> ((resolve) => {
      this.chatService.findChatHistoryBetweenUsers(this.username, this.otherUsername(), this.page).subscribe({
        next: response => {
          this.messages = response.content ?? [];
          this.totalMessages = response.totalElements;
          resolve();
        },
        error: err => {
          alert("Could not load chat history. Please try again later.");
          console.error(err);
          this.chatService.ngOnDestroy();
        }
      });
    });
  }

  private scrollToBottom(): void {
    this.scrollBar.nativeElement.scrollTop = this.scrollBar.nativeElement.scrollHeight;
  }
}
