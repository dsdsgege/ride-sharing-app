import {Component, inject, OnInit} from '@angular/core';
import {Button} from "primeng/button";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RatingModel} from '../../../model/rating-model';
import {SelectButton} from 'primeng/selectbutton';
import {RatingService} from '../../../services/rating-service';

@Component({
  selector: 'app-rating-tab-component',
  imports: [
    Button,
    FormsModule,
    ReactiveFormsModule,
    SelectButton
  ],
  templateUrl: './rating-tab-component.html',
  styleUrl: './rating-tab-component.scss'
})
export class RatingTabComponent implements OnInit {

  protected ratingOptions: string[] = ["Driver", "Passenger"];

  protected option: string = "Driver";

  protected page: number = 0;

  protected totalItems: number = 0;

  protected myRatings: RatingModel[] = [];

  private readonly ratingService = inject(RatingService);

  ngOnInit(): void {
  }

  loadMore() {
    console.log("load more");
  }
}
