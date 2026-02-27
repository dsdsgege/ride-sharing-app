import {
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import {Button} from 'primeng/button';
import {RideModel} from '../../../model/ride/ride-model';
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
  ],

  templateUrl: './drives-tab-component.html',
  styleUrl: './drives-tab-component.scss'
})
export class DrivesTabComponent implements OnInit {

  protected rides: RideModel[] = [];

  protected totalItems = 0;

  protected page = 0;

  protected carMakeControl: FormControl<string | null> = new FormControl(null);

  protected seatsControl: FormControl<number | null> = new FormControl(1);

  protected arriveControl: FormControl<Date | null> = new FormControl(null);

  protected departControl: FormControl<Date | null> = new FormControl(null);

  protected selectedRide = signal<RideModel | undefined>(undefined);

  protected loadingService = inject(LoadingService);

  protected readonly messageService = inject(MessageService);

  protected readonly driveService = inject(DriveService);

  private readonly cdr: ChangeDetectorRef = inject(ChangeDetectorRef);

  private readonly formService = inject(FormService);

  ngOnInit() {
    this.loadDrives();
  }

  protected selectRide(ride: RideModel) {
    this.selectedRide.set(ride);
    this.carMakeControl.setValue(ride.carMake ?? "");
    this.seatsControl.setValue(ride.seats ?? 1);
    this.arriveControl.setValue(new Date(ride.arrivalTime));
    this.departControl.setValue(new Date(ride.departureTime));

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

    ride.departureTime = this.departControl.getRawValue() ?? "";
    ride.arrivalTime = this.arriveControl.getRawValue() ?? "";
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
          detail: errorMessage ?? error.message ?? 'An error occurred.'});
        this.loadingService.hide();
        return;
      },
    });
  }
}
