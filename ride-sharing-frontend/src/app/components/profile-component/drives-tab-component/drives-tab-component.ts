import {
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import {Button} from 'primeng/button';
import {InputNumber} from 'primeng/inputnumber';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {DriveService} from '../../../services/drive-service';
import {DatePicker} from 'primeng/datepicker';
import {MessageService} from 'primeng/api';
import {HttpErrorResponse} from '@angular/common/http';
import {LoadingService} from '../../../services/loading-service';
import {Observable} from 'rxjs';
import {FormService} from '../../../services/form-service';
import {OverlayBadgeModule} from 'primeng/overlaybadge';
import {Tooltip} from 'primeng/tooltip';
import {RideModelWithPassengers} from '../../../model/ride/ride-model-with-passengers';
import {Badge} from 'primeng/badge';
import {Dialog} from 'primeng/dialog';
import {Rating} from 'primeng/rating';
import {AccordionModule} from 'primeng/accordion';
import {UserModel} from '../../../model/user-model';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faPaperPlane} from '@fortawesome/free-solid-svg-icons';
import {RatingService} from '../../../services/rating-service';


@Component({
  selector: 'app-drives-tab-component',
  imports: [
    Button,
    InputNumber,
    FormsModule,
    DatePipe,
    FloatLabel,
    InputText,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePicker,
    OverlayBadgeModule,
    Tooltip,
    Badge,
    Dialog,
    Rating,
    AccordionModule,
    FaIconComponent,
  ],

  templateUrl: './drives-tab-component.html',
  styleUrl: './drives-tab-component.scss'
})
export class DrivesTabComponent implements OnInit {

  protected rides: RideModelWithPassengers[] = [];

  protected totalItems = 0;

  protected page = 0;

  protected carMakeControl: FormControl<string | null> = new FormControl(null);

  protected seatsControl: FormControl<number | null> = new FormControl(1);

  protected arriveControl: FormControl<Date | null> = new FormControl(null);

  protected departControl: FormControl<Date | null> = new FormControl(null);

  protected dialogVisible: boolean = false;

  protected selectedRide = signal<RideModelWithPassengers | undefined>(undefined);

  protected loadingService = inject(LoadingService);

  protected readonly faPaperPlane = faPaperPlane;

  protected readonly messageService = inject(MessageService);

  protected readonly driveService = inject(DriveService);

  private readonly cdr: ChangeDetectorRef = inject(ChangeDetectorRef);

  private readonly formService = inject(FormService);

  private readonly ratingService = inject(RatingService);

  ngOnInit() {
    this.loadDrives();
  }

  protected selectRide(ride: RideModelWithPassengers) {
    this.selectedRide.set(ride);
    this.carMakeControl.setValue(ride.carMake ?? "");
    this.seatsControl.setValue(ride.seats ?? 1);
    this.arriveControl.setValue(new Date(ride.arrive));
    this.departControl.setValue(new Date(ride.depart));

    // so the styling of primeng and other components load too
    setTimeout(() => {
      this.cdr.detectChanges();
    });
  }

  protected updateDrive() {
    if (!confirm('Changes will be made to the selected drive. Are you sure?')) {
      this.messageService.add({severity: 'warn', summary: 'Update cancelled'})
      return;
    }

    const ride = this.selectedRide();
    if (!ride) {
      this.messageService.add({severity: 'warn', summary: 'No drive selected',
        detail: 'Please select a drive to update.'});
      return;
    }

    if (!this.formService.areInputsFilled(...[this.departControl, this.arriveControl, this.carMakeControl])) {
      this.messageService.add({severity: 'warn', summary: 'Update cancelled',
        detail: 'Please fill all the fields.'});
      return;
    }

    this.loadingService.show()

    ride.depart = this.departControl.getRawValue() ?? "";
    ride.arrive = this.arriveControl.getRawValue() ?? "";
    ride.carMake = this.carMakeControl.getRawValue() ?? "";
    ride.seats = this.seatsControl.getRawValue() ?? 0;

    this.subscribe(this.driveService.updateDrive(ride), 'The selected drive was updated successfully.',
      'Drive updated', 'An error occurred while updating the drive.')
  }

  protected deleteDrive() {
    if (!confirm('Are you sure you want to delete this drive?')) {
      this.messageService.add({severity: 'warn', summary: 'Deletion cancelled'})
      return;
    }

    const ride = this.selectedRide();
    if (!ride) {
      this.messageService.add({severity: 'warn', summary: 'No drive selected',
        detail: 'Please select a drive to delete.'});
      return;
    }

    this.loadingService.show();
    this.subscribe(this.driveService.deleteDrive(ride.id), 'The selected drive was deleted successfully.',
      'Drive deleted', 'An error occurred while deleting the drive.')
  }

  protected loadMore() {
    this.page++;
    this.loadDrives();
  }

  protected isPast(arrive: Date | string): boolean {
    return new Date(arrive) < new Date();
  }

  protected isPassengersUndefinedOrEmpty(passengers: UserModel[] | null | undefined): boolean {
    if (!passengers) {
      return true;
    }
    return passengers?.length === 0;
  }

  protected sendRating(passenger: UserModel, ratingComment: string) {
    if (ratingComment.length === 0) {
      this.messageService.add({severity: 'warn', summary: 'Rating cancelled',
        detail: 'Please enter a rating comment.'});
      return;
    }

    const ride = this.selectedRide();
    if (ride === undefined ) {
      this.messageService.add({severity: 'warn', summary: 'No drive selected',
        detail: 'Please select a drive to rate.'});
      return;
    }
    this.subscribe(this.ratingService.addRating(ride.id, passenger.username, passenger.rating, ratingComment),
      "Your rating is sent.", "Success", "An error occurred while sending your rating.");
  }

  private loadDrives() {
    this.driveService.findMyDrives(this.page).subscribe(response => {
      this.rides = response.content;
      this.totalItems = response.totalElements;
    });
  }

  private subscribe(obs: Observable<any>, successMessage: string, summary: string, errorMessage: string) {
    obs.subscribe({
      next: response => {
        if (response.success) {
          this.messageService.add({severity: 'success', summary: summary,
            detail: successMessage});
          this.loadDrives();
          this.loadingService.hide();
          return;
        }
      },
      error: (error: HttpErrorResponse) => {
        this.messageService.add({severity: 'error', summary: 'Error',
          detail: error.error.message ?? errorMessage});
        this.loadingService.hide();
        return;
      },
    });
  }
}
