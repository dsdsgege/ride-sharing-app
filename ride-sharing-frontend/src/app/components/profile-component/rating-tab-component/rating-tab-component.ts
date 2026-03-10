import {Component, effect, inject, signal, WritableSignal} from '@angular/core';
import {Button} from "primeng/button";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RatingModel} from '../../../model/rating-model';
import {SelectButton} from 'primeng/selectbutton';
import {RatingService} from '../../../services/rating-service';
import {MessageService} from 'primeng/api';
import {Rating} from 'primeng/rating';
import {Tooltip} from 'primeng/tooltip';
import {Observable} from 'rxjs';
import {RatingResponseModel} from '../../../model/rating-response-model';
import {HttpErrorResponse} from '@angular/common/http';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faInfoCircle} from '@fortawesome/free-solid-svg-icons/faInfoCircle';
import {Popover} from 'primeng/popover';

@Component({
  selector: 'app-rating-tab-component',
  imports: [
    Button,
    FormsModule,
    ReactiveFormsModule,
    SelectButton,
    Rating,
    Tooltip,
    FaIconComponent,
    Popover
  ],
  templateUrl: './rating-tab-component.html',
  styleUrl: './rating-tab-component.scss'
})
export class RatingTabComponent {

  protected givenOptions: string[] = ["Driver", "Passenger"];

  protected givenOption: WritableSignal<string> = signal("Driver");

  protected givenRatings: RatingModel[] = [];

  protected givenPage: number = 0;

  protected givenTotal: number = 0;

  protected receivedOptions: string[] = ["Driver", "Passenger"];

  protected receivedOption: WritableSignal<string> = signal("Driver");

  protected receivedRatings: RatingModel[] = [];

  protected receivedPage: number = 0;

  protected receivedTotal: number = 0;

  protected readonly faInfoCircle = faInfoCircle;

  private readonly ratingService = inject(RatingService);

  private readonly messageService = inject(MessageService);

  constructor() {
    effect(() => {
      // RECEIVED RATINGS
      let receivedObs;
      if (this.receivedOption() === "Driver") {
        receivedObs = this.ratingService.getReceivedAsDriver(this.receivedPage);
      } else if (this.receivedOption() === "Passenger") {
        receivedObs = this.ratingService.getReceivedAsPassenger(this.receivedPage);
      }
      if (receivedObs) {
        this.loadRatings(receivedObs, "Could not load received ratings.", (response) => {
          this.receivedRatings = response.content;
          this.receivedTotal = response.totalElements;
        });
      }

      // GIVEN RATINGS
      let givenObs;
      if (this.givenOption() == "Driver") {
        givenObs = this.ratingService.getGivenAsDriver(this.givenPage);
      } else if (this.givenOption() == "Passenger") {
        givenObs = this.ratingService.getGivenAsPassenger(this.givenPage);
      }

      if (givenObs) {
        this.loadRatings(givenObs, "Could not load given ratings.", (response) => {
          this.givenRatings = response.content;
          this.givenTotal = response.totalElements;
        });
      }
    });
  }

  protected loadMoreReceived() {
    this.receivedPage++;
    let receivedObs;
    if (this.receivedOption() === "Driver") {
      receivedObs = this.ratingService.getReceivedAsDriver(this.receivedPage);
    } else if (this.receivedOption() === "Passenger") {
      receivedObs = this.ratingService.getReceivedAsPassenger(this.receivedPage);
    }

    if (!receivedObs) {
      return;
    }

    this.loadRatings(receivedObs, "Could not load received ratings.", (response) => {
      this.receivedRatings = response.content;
      this.receivedTotal = response.totalElements;
    });
  }

  protected loadMoreGiven() {
    this.givenPage++;
    let givenObs;
    if (this.givenOption() == "Driver") {
      givenObs = this.ratingService.getGivenAsDriver(this.givenPage);
    } else if (this.givenOption() == "Passenger") {
      givenObs = this.ratingService.getGivenAsPassenger(this.givenPage);
    }

    if (!givenObs) {
      return;
    }

    this.loadRatings(givenObs, "Could not load given ratings.", (response) => {
      this.givenRatings = response.content;
      this.givenTotal = response.totalElements;
    });
  }

  //TODO: DELETE/EDIT RATING

  // callback that will update the ratings...
  private loadRatings(obs: Observable<RatingResponseModel>, errorMessage: string,
                      onSuccess: (data: RatingResponseModel) => void) {

    obs.subscribe({
      next: (response) => {
        onSuccess(response);
      },
      error: (err: HttpErrorResponse) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err.error?.message ?? errorMessage
        });
      }
    });
  }
}
