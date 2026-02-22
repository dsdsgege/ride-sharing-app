import {Component, inject, model, ModelSignal, OnInit, signal, WritableSignal} from '@angular/core';
import {Button} from "primeng/button";
import {ChatComponent} from "../../chat-component/chat-component";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faUser} from "@fortawesome/free-solid-svg-icons/faUser";
import {ProfileDataModel} from '../../../model/profile-data-model';
import {ChatService} from '../../../services/chat-service';
import {UserService} from '../../../services/user-service';

@Component({
  selector: 'app-chat-tab-component',
    imports: [
        Button,
        ChatComponent,
        FaIconComponent
    ],
  templateUrl: './chat-tab-component.html',
  styleUrl: './chat-tab-component.scss'
})
export class ChatTabComponent implements OnInit {

  isLoading: ModelSignal<boolean> = model(false);

  profile: ModelSignal<ProfileDataModel> = model.required<ProfileDataModel>();

  protected readonly faUser = faUser;

  protected partners: ProfileDataModel[] = [];

  protected selectedChatPartner: WritableSignal<ProfileDataModel | undefined> = signal(undefined);

  protected totalItems = 0;

  protected page = 0;

  protected readonly chatService = inject(ChatService);

  protected readonly userService = inject(UserService);

  ngOnInit(): void {
    console.log(this.profile());
    console.log(this.partners);
    // this way we only stop loading after everything is fetched
    this.findChatPartners().then(() => {
      this.isLoading.set(false);
    });
  }

  protected async loadMore() {
    this.page++;

    this.isLoading.set(true);
    await this.findChatPartners().then(() =>
      this.isLoading.set(false)
    );
  }

  protected chatWithPartner(partner: ProfileDataModel) {
    this.selectedChatPartner.set(partner);
  }

  private findChatPartners(): Promise<void> {
    return new Promise((resolve) => {
      this.chatService.findChatPartnersByUsername(this.profile().username, this.page).subscribe(response => {
        let usernames = response.content ?? [];

        this.userService.findUsersByUsernames(usernames).subscribe(response => {
          this.partners = response.users;
          resolve();
        });
      });
    });
  }
}
