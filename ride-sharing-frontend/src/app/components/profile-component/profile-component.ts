import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {ProfileDataModel} from '../../model/profile-data-model';
import {RideService} from '../../services/ride-service';
import {DriveService} from '../../services/drive-service';
import Keycloak from 'keycloak-js';
import {catchError, forkJoin, of} from 'rxjs';
import {ProfileTab} from '../../model/profile-tab';
import {ChatTabComponent} from './chat-tab-component/chat-tab-component';
import {DrivesTabComponent} from './drives-tab-component/drives-tab-component';
import {LoadingService} from '../../services/loading-service';
import {finalize} from 'rxjs/operators';
import {RatingTabComponent} from './rating-tab-component/rating-tab-component';

@Component({
  selector: 'app-profile-component',
  imports: [ChatTabComponent, DrivesTabComponent, RatingTabComponent],
  templateUrl: './profile-component.html',
  styleUrl: './profile-component.scss'
})
export class ProfileComponent implements OnInit {

  protected profile: ProfileDataModel = new ProfileDataModel();

  protected selectedProfileTab: WritableSignal<ProfileTab> = signal(ProfileTab.CHATS);

  protected readonly rideService: RideService = inject(RideService);

  protected readonly driveService: DriveService = inject(DriveService);

  protected readonly keycloak: Keycloak = inject(Keycloak);

  protected readonly loadingService = inject(LoadingService);

  protected readonly ProfileTab = ProfileTab;

  ngOnInit(): void {
    this.loadingService.show();
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
      }).pipe(
        finalize(() => this.loadingService.hide())
      ).subscribe({
        next: res => {
          this.profile.rides = res.rides;
          this.profile.drives = res.drives;
          this.profile.rating = res.rating;
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
