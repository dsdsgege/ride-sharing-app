import {
  ChangeDetectorRef,
  Component,
  inject, model,
  OnInit,
  signal
} from '@angular/core';
import {Button} from 'primeng/button';
import {RideModel} from '../../../model/ride/ride-model';
import {InputNumber} from 'primeng/inputnumber';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {DriveService} from '../../../services/drive-service';
import {DatePicker} from 'primeng/datepicker';
import {MessageService} from 'primeng/api';
import {HttpErrorResponse} from '@angular/common/http';

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

  protected driveEditform: FormGroup = new FormGroup({
    carMakeControl: this.carMakeControl,
    seatsControl: this.seatsControl,
    arriveControl: this.arriveControl,
    departControl: this.departControl,
  });

  protected selectedDrive = signal<RideModel | undefined>(undefined);

  protected loading = model(false);

  protected readonly messageService = inject(MessageService);

  protected readonly driveService = inject(DriveService);

  private readonly cdr: ChangeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit() {
    this.loadDrives();
  }

  protected selectRide(ride: RideModel) {
    this.selectedDrive.set(ride);
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

  }

  protected deleteDrive() {
    if (!confirm('Are you sure you want to delete this drive?')) {
      this.messageService.add({severity: 'warn', summary: 'Deletion cancelled'})
      return;
    }

    const drive = this.selectedDrive();
    if (!drive) {
      this.messageService.add({severity: 'warn', summary: 'No drive selected',
        detail: 'Please select a drive to delete.'});
      return;
    }

    this.loading.set(true);
    this.driveService.deleteDrive(drive.id).subscribe({
      next: response => {
        if (response.success) {
          this.messageService.add({severity: 'success', summary: 'Drive deleted',
            detail: 'The selected drive was deleted successfully.'});
          this.loadDrives();
          this.loading.set(false);
          return;
        }
      },
      error: (error: HttpErrorResponse) => {
        this.messageService.add({severity: 'error', summary: 'Error',
          detail: error.message ?? 'An error occurred while deleting the drive.'});
        this.loading.set(false);
        return;
      },
    });
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
}
