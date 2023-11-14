import { CUSTOM_ELEMENTS_SCHEMA, Component, Inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../axios.service';
import { AxiosResponse } from 'axios';
import { RedirectService } from '../redirect.service';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-event-info',
  templateUrl: './event-info.component.html',
  styleUrls: ['./event-info.component.css'],
  standalone: true,
  imports: [MatButtonModule, MatDialogModule, MatCardModule, MatIconModule, CommonModule, MatMenuModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class EventInfoComponent implements OnInit {
  event: any;
  registrationButtonLabel: string = 'Register';
  isRegistered: boolean = false;
  showPeopleList: boolean = false;

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private axiosService: AxiosService,
    private router: Router,
    private service: RedirectService,
  ) {}

  ngOnInit() {
    this.axiosService.authenticate();

    if (!this.event) {
      // Se l'evento non è stato fornito, crea un evento di esempio
      this.event = {
        id: '',
        title: '',
        category: '',
        description: '',
        address: '',
        date: new Date().toISOString(),
      };
    }
    this.route.params.subscribe((params) => {
      const id = +params['id'];
      this.axiosService
        .request('GET', `/api/event/findById/${id}`, {})
        .then((response) => {
          console.log('OK');
          this.event = response.data;
          console.log(this.event);
          const imageURLs = this.event.imageURL;
          const imageURLPromises = imageURLs.map((url: string) =>
            this.loadURL(url)
          );
          Promise.all(imageURLPromises)
            .then((urls) => {
              this.event.imageURL = urls;
            })
            .catch((error) => {
              console.error('Error loading image URLs:', error);
            });
          if (
            this.isAlreadyLogged(
              this.event.participants,
              window.localStorage.getItem('userId')
            ) === true
          ) {
            console.log(true);
            this.isRegistered = true;
          } else {
            console.log(false);
            this.isRegistered = false;
          }
        })
        .catch((error) => {
          console.log(error);
          this.router.navigate(['/event-board']);
        });
    });
  }

  loadURL(endpoint: string): Promise<string> {
    return new Promise((resolve, reject) => {
      this.axiosService.customGet(endpoint).subscribe(
        (response: AxiosResponse) => {
          const reader = new FileReader();
          reader.onload = () => {
            resolve(reader.result as string);
          };
          reader.readAsDataURL(response.data);
        },
        (error) => {
          console.error('Error retrieving the image:', error);
          reject(error);
        }
      );
    });
  }

  checkMyEvents() {
    if (this.service.getRedirect() == '/my-events') {
      return true;
    }
    return false;
  }

  isAlreadyLogged(participants: Array<any>, userId: string | null): boolean {
    if (!userId) {
      return false;
    }

    for (const participant of participants) {
      if (participant.id.toString() === userId) {
        return true;
      }
    }

    return false;
  }

  toggleRegistration() {
    this.axiosService.authenticate();
    this.route.params.subscribe((params) => {
      const eventId = +params['id'];
      const userId = window.localStorage.getItem('userId');
      this.isRegistered = !this.isRegistered;
      this.registrationButtonLabel = this.isRegistered ? 'Unregister' : 'Register';

      // Chiamata per aggiungere/rimuovere partecipante
      const requestMethod = this.isRegistered ? 'POST' : 'DELETE';
      const requestEndpoint = this.isRegistered ? `/api/event/${eventId}/register/${userId}` : `/api/event/${eventId}/unregister/${userId}`;

      this.axiosService
        .request(requestMethod, requestEndpoint, {})
        .then(() => {
          console.log('Registration request succeeded');
          // Altra chiamata per aggiornare la lista di partecipanti
          this.updateParticipantsList(eventId);
        })
        .catch((error) => {
          console.error('Registration request failed', error);
          this.router.navigate(['/event-board']);
        });
    });
  }

  updateParticipantsList(eventId: number) {
    this.axiosService.authenticate();
    this.axiosService
      .request('GET', `/api/event/findById/${eventId}`, {})
      .then((response) => {
        console.log('Fetched updated participants list');
        this.event.participants = response.data.participants;
        console.log(this.event.participants);
      })
      .catch((error) => {
        console.error('Error fetching updated participants list', error);
      });
  }

  getButtonClass() {
    return this.isRegistered ? 'red-button' : 'join-button';
  }

  deleteEvent() {
    // Qui puoi inserire la logica per la cancellazione dell'evento
    // Ad esempio, puoi aprire un dialogo di conferma per la cancellazione
    // e gestire l'eliminazione effettiva dell'evento
    console.log('Deleting event...');

    this.route.params.subscribe((params) => {
      const eventId = +params['id'];
      const userId = window.localStorage.getItem('userId');
      this.axiosService
        .request('DELETE', `/api/event/${eventId}/delete-event/${userId}`, {})
        .then((response) => {
          console.log('Event deleted');
          this.router.navigate(['/event-board']);
        })
        .catch((error) => {
          console.error('Error deleting event', error);
        });
    });
  }

  openDialog() {
    console.log(this.event.participants);

    // Passiamo i partecipanti al componente del dialogo PeopleList
    const dialogRef = this.dialog.open(PeopleList, {
      data: { participants: this.event.participants },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log(`Dialog result: ${result}`);
    });
  }

  editEvent() {
    this.route.params.subscribe((params) => {
      const eventId = +params['id'];
      this.router.navigate(['/event-edit', eventId]);
    });
  }

}


@Component({
  selector: 'dialog-content-example-dialog',
  templateUrl: './people-list.html',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatListModule, CommonModule],
})
export class PeopleList {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { participants: any[] }) {}
}
