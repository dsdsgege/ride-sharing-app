import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {ProfileDataModel} from '../../model/profile-data-model';
import {RideService} from '../../services/ride-service';
import {DriveService} from '../../services/drive-service';
import {ProgressBar} from 'primeng/progressbar';
import Keycloak from 'keycloak-js';
import {catchError, forkJoin, of} from 'rxjs';
import {ProfileTab} from '../../model/profile-tab';
import {ChatTabComponent} from './chat-tab-component/chat-tab-component';
import {DrivesTabComponent} from './drives-tab-component/drives-tab-component';
import {Toast} from 'primeng/toast';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-profile-component',
  imports: [ProgressBar, ChatTabComponent, DrivesTabComponent, Toast],
  templateUrl: './profile-component.html',
  providers: [MessageService],
  styleUrl: './profile-component.scss'
})
export class ProfileComponent implements OnInit {

  protected isLoading: WritableSignal<boolean> = signal(true);

  protected profile: ProfileDataModel = new ProfileDataModel();

  protected selectedProfileTab: WritableSignal<ProfileTab> = signal(ProfileTab.CHATS);

  protected readonly rideService: RideService = inject(RideService);

  protected readonly driveService: DriveService = inject(DriveService);

  protected readonly keycloak: Keycloak = inject(Keycloak);

  protected readonly ProfileTab = ProfileTab;

  protected readonly messageService = inject(MessageService);

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
        },
        error: (err) => {
          console.error(err);
          this.isLoading.set(false);
        }
      });
    });
  }
}
