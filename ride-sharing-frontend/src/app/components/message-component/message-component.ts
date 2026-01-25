import {Component, input, model, output} from '@angular/core';
import {faArrowCircleUp} from '@fortawesome/free-solid-svg-icons/faArrowCircleUp';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faXmark} from '@fortawesome/free-solid-svg-icons/faXmark';
import {faUser} from '@fortawesome/free-solid-svg-icons/faUser';

@Component({
  selector: 'app-message-component',
  standalone: true,
  imports: [
    FaIconComponent
  ],
  templateUrl: './message-component.html',
  styleUrl: './message-component.scss'
})
export class MessageComponaent {
  public name = input.required<string>();

  public isExpanded = model<boolean>(false);

  public close = output<void>();

  protected readonly faArrowCircleUp = faArrowCircleUp;
  protected readonly faXmark = faXmark;
  protected readonly faUser = faUser;
}
