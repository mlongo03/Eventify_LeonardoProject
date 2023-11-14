import { Component, Injectable, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AxiosService } from '../axios.service';
import { RedirectService } from '../redirect.service';
import { HostListener } from '@angular/core';
import { SseService } from '../SseService';

@Injectable()
@Component({
	selector: 'app-header',
	templateUrl: './header.component.html',
	styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit{

  redirection = '';
  numeroNotifiche: any = 0; // Initialize to 0
  notifications: any[] = []; // Store the notification list
  titleWeb: string = '/assets/title.png';
  titleMobile: string = '/assets/titlemobile.png';
  isMobile: boolean = false;
  profilePhoto: string = '';

  constructor(
    private router: Router,
    private redirectService: RedirectService,
    protected axiosService: AxiosService,
    private sseService: SseService
  ) {
      this.sseService.getEvents().subscribe((eventData) => {
        //aggiornare il numero di notifiche
        console.log("arrived eventData");
        for (const event of eventData) {
          console.log(event.dateTime);
        }
        this.notifications = eventData;
        console.log("pushed inside notification");
        for (const notification of this.notifications) {
          console.log(notification.dateTime);
        }
        this.numeroNotifiche = 0;
        for (const notification of this.notifications) {
          if (notification.read == false) {
            this.numeroNotifiche++;
          }
        }
      });
    this.isMobile = window.innerWidth < 768;
  }

  ngOnInit(): void {
    // this.axiosService.authenticate();
    if (this.redirectService.getIsLogged() === true) {
      const userId = window.localStorage.getItem("userId");
      this.axiosService
      .request('GET', `/show-notification/${userId}`, {})
      .then((response) => {
        console.log('test');
        this.notifications = response.data;
        console.log("pushed inside notification");
        for (const notification of this.notifications) {
          console.log(notification.dateTime);
        }
        this.numeroNotifiche = 0;
        for (const notification of this.notifications) {
          if (notification.read == false) {
            this.numeroNotifiche++;
          }
        }
      })
      .catch((error) => {
        console.log('error');
      });
    }
  }


  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    this.isMobile = window.innerWidth < 768;
  }

  logout() {
    this.axiosService
      .request2('POST', 'api/auth/logout', {})
      .then((response) => {
        this.redirectService.setIsLogged(false);
        this.router.navigate(['/login']);
      })
      .catch((error) => {
        this.redirectService.setIsLogged(false);
        this.router.navigate(['/login']);
      });
  }

  getIsLogged(): boolean {
    // console.log(this.redirectService.getIsLogged());
    return this.redirectService.getIsLogged();
  }

  updateRedirection(newRedirection: string) {
    this.redirection = newRedirection;
  }

  authenticate() {
    this.axiosService
      .request('POST', 'api/authenticate', {})
      .then((response) => {
        this.redirectService.setIsLogged(true);
        console.log('test');
        this.router.navigate([this.redirection]);
      })
      .catch((error) => {
        this.redirectService.setIsLogged(false);
        console.log('error');
        this.router.navigate(['/login']);
      });
  }

  resetNumeroNotifiche() {
    this.numeroNotifiche = 0;
  }

  navigateTo(route: string) {
    this.redirectService.setRedirect(route);
    this.router.navigate([route]);
  }

  navigateToMyEvents() {
    this.redirectService.setRedirect('/my-events');
    console.log(this.redirectService.getRedirect());
    this.router.navigate(['/event-board']);
  }

  navigateToRegisteredEvents() {
    this.redirectService.setRedirect('/registered-events');
    console.log(this.redirectService.getRedirect());
    this.router.navigate(['/event-board']);
  }

  SetNotificationAsRead() {
    console.log("click on notification menu");

    const userId = window.localStorage.getItem("userId");
    this.axiosService.request(
      "POST",
      `/setNotificationRead/${userId}`,
      {
      });
      this.numeroNotifiche = 0;

  }

}
