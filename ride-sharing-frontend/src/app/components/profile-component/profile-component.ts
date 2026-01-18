import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {ProfileDataModel} from '../../model/profile-data-model';
import {RideService} from '../../services/ride-service';
import {DriveService} from '../../services/drive-service';
import {ProgressBar} from 'primeng/progressbar';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-profile-component',
  imports: [ProgressBar],
  templateUrl: './profile-component.html',
  styleUrl: './profile-component.scss'
})
export class ProfileComponent implements OnInit {

  protected isLoading: WritableSignal<boolean> = signal(true);

  protected profile: ProfileDataModel = new ProfileDataModel();

  protected readonly rideService: RideService = inject(RideService);
  protected readonly driveService: DriveService = inject(DriveService);

  protected readonly keycloak: Keycloak = inject(Keycloak);

  ngOnInit(): void {
    this.keycloak.loadUserProfile().then(profile => {
      this.profile.fullName = profile.firstName + ' ' + profile.lastName;

      this.rideService.findRideCountByFullName(this.profile.fullName).subscribe(res => this.profile.rides = res);

      this.driveService.findDriveCountByFullName(this.profile.fullName).subscribe(res => this.profile.drives = res);

      this.driveService.findDriverRatingByFullName(this.profile.fullName).subscribe(res => this.profile.rating = res);

      this.isLoading.set(false);
    });
  }
}
