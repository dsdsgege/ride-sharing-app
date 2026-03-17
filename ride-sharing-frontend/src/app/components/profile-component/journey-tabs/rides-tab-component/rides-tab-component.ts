import {ChangeDetectorRef, Component, inject, OnInit, signal} from '@angular/core';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from "primeng/accordion";
import {Button} from "primeng/button";
import {CurrencyPipe, DatePipe} from "@angular/common";
import {Dialog} from "primeng/dialog";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {Rating} from "primeng/rating";
import {LoadingService} from '../../../../services/loading-service';
import {MessageService} from 'primeng/api';
import {RatingService} from '../../../../services/rating-service';
import {UserModel} from '../../../../model/user-model';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {RideModel} from '../../../../model/ride/ride-model';
import {RideService} from '../../../../services/ride-service';
import {faPaperPlane} from '@fortawesome/free-solid-svg-icons';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-rides-tab-component',
  imports: [
    Accordion,
    AccordionContent,
    AccordionHeader,
    AccordionPanel,
    Button,
    CurrencyPipe,
    DatePipe,
    Dialog,
    FaIconComponent,
    Rating,
    FormsModule
  ],
  templateUrl: './rides-tab-component.html',
  styleUrl: '../journey-tabs.scss'
})
export class RidesTabComponent implements OnInit {

  protected rides: RideModel[] = [];

  protected totalItems = 0;

  protected page = 0;

  protected dialogVisible: boolean = false;

  protected selectedRide = signal<RideModel | undefined>(undefined);

  protected loadingService = inject(LoadingService);

  protected readonly faPaperPlane = faPaperPlane;

  protected readonly messageService = inject(MessageService);

  protected readonly rideService = inject(RideService);

  private readonly cdr: ChangeDetectorRef = inject(ChangeDetectorRef);

  private readonly ratingService = inject(RatingService);

  ngOnInit() {
    this.loadRides();
  }

  protected selectRide(ride: RideModel) {
    this.selectedRide.set(ride);

    // so the styling of primeng and other components load too
    setTimeout(() => {
      this.cdr.detectChanges();
    });
  }

  protected cancelRide() {
    if (!confirm('Are you sure you want to leave this ride?')) {
      this.messageService.add({severity: 'warn', summary: 'Leave cancelled'})
      return;
    }

    this.rideService.cancelRide(this.selectedRide()!.id).subscribe({

    });
  }

  protected loadMore() {
    this.page++;
    this.loadRides();
  }

  protected isPast(arrive: Date | string): boolean {
    return new Date(arrive) < new Date();
  }

  protected sendRating(driver: UserModel, ratingComment: string) {
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
    this.subscribe(this.ratingService.addRatingAsPassenger(ride.id, driver.username, driver.rating, ratingComment),
      "Your rating is sent.", "Success", "An error occurred while sending your rating.");
  }

  protected isRideReadyToRate(): boolean {
    const ride = this.selectedRide();

    if (!ride || !ride.depart) return false;

    // Convert the departure to a timestamp and compare it to right now
    const departTime = new Date(ride.depart).getTime();
    return departTime < Date.now();
  }

  private loadRides() {
    this.rideService.getMyRides(this.page).subscribe(response => {
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
          this.loadRides();
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
