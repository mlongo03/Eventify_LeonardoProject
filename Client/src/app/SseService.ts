import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private eventSource: EventSource | undefined;
  private sseSubject: Subject<any> = new Subject<any>();

  constructor() {
    console.log('connecting to SSE');
    this.connectToSSE();
  }

  public getEvents(): Observable<any> {
    return this.sseSubject.asObservable();
  }

  private connectToSSE() {
    const userId = window.localStorage.getItem('userId');
    this.eventSource = new EventSource(`https://localhost:8443/teller/subscribe/${userId}`);
    console.log('Creating EventSource');

    this.eventSource.onmessage = (event) => {
      console.log('Received event', event);

      // Parse the incoming JSON data into JavaScript object
      const eventData = JSON.parse(event.data);

      // Push the parsed object into the subject
      this.sseSubject.next(eventData);
    };

    this.eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      // You can handle SSE errors here, e.g., reconnect or show an error message to the user
    };
  }
}
