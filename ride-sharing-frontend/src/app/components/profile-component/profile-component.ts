import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {ProfileDataModel} from '../../model/profile-data-model';
import {RideService} from '../../services/ride-service';
import {DriveService} from '../../services/drive-service';
import Keycloak from 'keycloak-js';
import {catchError, forkJoin, of} from 'rxjs';
import {ProfileTab} from '../../model/profile-tab';
import {ChatTabComponent} from './chat-tab-component/chat-tab-component';
import {DrivesTabComponent} from './journey-tabs/drives-tab-component/drives-tab-component';
import {LoadingService} from '../../services/loading-service';
import {finalize} from 'rxjs/operators';
import {RatingTabComponent} from './rating-tab-component/rating-tab-component';
import {RatingService} from '../../services/rating-service';
import {ChatService} from '../../services/chat-service';
import {RidesTabComponent} from './journey-tabs/rides-tab-component/rides-tab-component';

@Component({
  selector: 'app-profile-component',
  imports: [ChatTabComponent, DrivesTabComponent, RatingTabComponent, RidesTabComponent],
  templateUrl: './profile-component.html',
  styleUrl: './profile-component.scss'
})
export class ProfileComponent implements OnInit {

  protected profile: ProfileDataModel = new ProfileDataModel();

  protected selectedProfileTab: WritableSignal<ProfileTab> = signal(ProfileTab.CHATS);

  protected readonly ProfileTab = ProfileTab;

  private readonly rideService: RideService = inject(RideService);

  private readonly driveService: DriveService = inject(DriveService);

  private readonly ratingService = inject(RatingService);

  private readonly chatService = inject(ChatService);

  private readonly keycloak: Keycloak = inject(Keycloak);

  private readonly loadingService = inject(LoadingService);

  ngOnInit(): void {
    this.loadingService.show();
    this.keycloak.loadUserProfile().then(profile => {
      this.profile.username = profile.username ?? "";
      this.profile.fullName = profile.firstName + ' ' + profile.lastName;

      forkJoin({
        rides: this.rideService.getRideCountByUsername(this.profile.username).pipe(
          catchError(err => of(0))
        ),
        drives: this.driveService.getDriveCountByUsername(this.profile.username).pipe(
          catchError(err => of(0))
        ),
        rating: this.ratingService.getMyRatingCount().pipe(
          catchError(err => of(0))
        ),
        chats: this.chatService.getMyPartnerCount().pipe(
          catchError(err => of(0))
        )
      }).pipe(
        finalize(() => this.loadingService.hide())
      ).subscribe({
        next: res => {
          this.profile.rides = res.rides;
          this.profile.drives = res.drives;
          this.profile.rating = res.rating;
          this.profile.chats = res.chats;
        },
        error: (err) => {
          console.error(err);
        }
      });
    }).catch(err => {
      console.error(err);
      this.loadingService.hide();
    });
  }
}
