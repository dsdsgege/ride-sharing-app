import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {ProfileDataModel} from '../../model/profile-data-model';
import {RideService} from '../../services/ride-service';
import {DriveService} from '../../services/drive-service';
import {ProgressBar} from 'primeng/progressbar';
import Keycloak from 'keycloak-js';
import {ChatService} from '../../services/chat-service';
import {Button} from 'primeng/button';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faUser} from '@fortawesome/free-solid-svg-icons/faUser';
import {ChatComponent} from '../chat-component/chat-component';
import {UserService} from '../../services/user-service';
import {catchError, forkJoin, of} from 'rxjs';

@Component({
  selector: 'app-profile-component',
  imports: [ProgressBar, Button, FaIconComponent, ChatComponent],
  templateUrl: './profile-component.html',
  styleUrl: './profile-component.scss'
})
export class ProfileComponent implements OnInit {

  protected isLoading: WritableSignal<boolean> = signal(true);

  protected profile: ProfileDataModel = new ProfileDataModel();

  protected partners: ProfileDataModel[] = [];

  protected selectedChatPartner: WritableSignal<ProfileDataModel | undefined> = signal(undefined);

  protected page = 0;

  protected totalItems = 0;

  protected readonly faUser = faUser;

  protected readonly rideService: RideService = inject(RideService);

  protected readonly driveService: DriveService = inject(DriveService);

  protected readonly chatService = inject(ChatService);

  protected readonly userService = inject(UserService);

  protected readonly keycloak: Keycloak = inject(Keycloak);

  ngOnInit(): void {
    this.keycloak.loadUserProfile().then(profile => {
      this.profile.username = profile.username ?? "";
      this.profile.fullName = profile.firstName + ' ' + profile.lastName;

      forkJoin({
        rides: this.rideService.findRideCountByUsername(this.profile.username).pipe(
          catchError(err => of(0))
        ),
        drives: this.driveService.findDriveCountByUsername(this.profile.username).pipe(
          catchError(err => of(0))
        ),
        rating: this.driveService.findDriverRatingByUsername(this.profile.username).pipe(
          catchError(err => of(0))
        )
      }).subscribe({
        next: res => {
          this.profile.rides = res.rides;
          this.profile.drives = res.drives;
          this.profile.rating = res.rating;

          // this way we only stop loading after everything is fetched
          this.findChatPartners().then(() => {
            this.isLoading.set(false);
          });
        },
        error: (err) => {
          console.error(err);
          this.isLoading.set(false);
        }
      });
    });
  }

  protected async loadMore() {
    this.page++;

    this.isLoading.set(true);
    await this.findChatPartners().then(() =>
      this.isLoading.set(false)
    );
  }

  chatWithPartner(partner: ProfileDataModel) {
    this.selectedChatPartner.set(partner);
  }

  private findChatPartners(): Promise<void> {
    return new Promise((resolve) => {
      this.chatService.findChatPartnersByUsername(this.profile.username, this.page).subscribe(response => {
        let usernames = response.content ?? [];

        this.userService.findUsersByUsernames(usernames).subscribe(response => {
          this.partners = response.users;
          resolve();
        })
      });
    });
  }
}
