import { Component } from '@angular/core';
import {Button} from "primeng/button";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RatingModel} from '../../../model/rating-model';
import {SelectButton} from 'primeng/selectbutton';

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
export class RatingTabComponent {

  protected ratingOptions: string[] = ["Driver", "Passenger"];

  protected option: string = "Driver";

  protected page: number = 0;

  protected totalItems: number = 0;

  protected myRatings: RatingModel[] = [];

  loadMore() {
    console.log("load more");
  }
}
