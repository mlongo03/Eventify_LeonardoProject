import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-event-blank',
  templateUrl: './event-blank.component.html',
  styleUrls: ['./event-blank.component.css']
})
export class EventBlankComponent {

	constructor(private router: Router) {}

	createEvent() {
		this.router.navigate(['/event-creation']);
	}
}