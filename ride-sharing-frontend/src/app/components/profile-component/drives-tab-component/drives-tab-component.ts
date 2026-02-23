import {ChangeDetectorRef, Component, inject, OnInit, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {RideModel} from '../../../model/ride/ride-model';
import {InputNumber} from 'primeng/inputnumber';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {DriveService} from '../../../services/drive-service';
import {DatePicker} from 'primeng/datepicker';

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

  protected readonly dateFormatForPicker: string = "short" // M/d/yy, h:mm a

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
