import {Component, inject, OnInit} from '@angular/core';
import {Button} from 'primeng/button';
import {ActivatedRoute} from '@angular/router';
import {DriveService} from '../../../services/drive-service';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-accept-passenger-component',
  imports: [
    Button
  ],
  providers: [MessageService],
  templateUrl: './accept-passenger-component.html',
})
export class AcceptPassengerComponent implements OnInit {

  private token: string | undefined = undefined;

  private readonly route = inject(ActivatedRoute);

  private readonly driveService: DriveService = inject(DriveService);

  private readonly messageService: MessageService = inject(MessageService);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });

    if (!this.token) {
      alert("This page requires a token.");
    }
  }

  onAccept() {
    this.driveService.acceptPassenger(this.token).subscribe(status => {
      if (status.success) {
        this.messageService.add({severity: 'success', summary: 'Successfully accepted'});
      } else {
        this.messageService.add({severity: 'error', summary: 'We could not accept the passenger'});
      }
    });
  }
}
