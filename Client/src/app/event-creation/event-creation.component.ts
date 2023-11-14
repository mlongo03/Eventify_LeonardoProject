import { Component, OnInit } from '@angular/core';
import { AxiosService } from '../axios.service';
import { FormBuilder, FormGroup, Validators, AbstractControl, NgControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-event-creation',
  templateUrl: './event-creation.component.html',
  styleUrls: ['./event-creation.component.css'],
})
export class EventCreationComponent implements OnInit{
  newEvent: any = {};
  imageFiles: File[] = []; // To store selected image files
  fieldFocused: boolean = false;
  fieldTouched: boolean = false;
  fieldFocused2: boolean = false;
  fieldTouched2: boolean = false;
  fieldFocused3: boolean = false;
  fieldTouched3: boolean = false;
  fieldFocused4: boolean = false;
  fieldTouched4: boolean = false;
  eventForm: FormGroup;
  customPattern = /.*?,.*?,.*\d.*\d.*\d.*\d.*\d./;

constructor(private axiosService: AxiosService, private formBuilder: FormBuilder, private router: Router) {
    this.eventForm = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required
        ]
      ],
      description: [
        '',
        [
          Validators.required
        ]
      ],
      place: [
        '',
        [
          Validators.required,
          Validators.pattern(this.customPattern)
        ]
      ],
      dateTime: [
        '',
        [
          Validators.required,
          this.DateRangeValidator()
        ]
      ],
      photo: [
        '',
        [
          Validators.required
        ]
      ],
      category: [
        '',
        [
          Validators.required
        ]
      ]
    })
  }

  ngOnInit(): void {
	  this.axiosService.authenticate();
  }

DateRangeValidator(): Validators {
  return (control: AbstractControl): { [key: string]: boolean } | null => {
    if (control.value) {
      const selectedDate = new Date(control.value);
      const currentDate = new Date();
      const minDate = new Date(currentDate);
      minDate.setDate(currentDate.getDate()+ 1);
      const maxDate = new Date(currentDate);
      maxDate.setFullYear(currentDate.getFullYear() + 1);

      if (selectedDate >= minDate && selectedDate <= maxDate) {
        return null;
      } else {
        return { dateRange: true };
      }
    }
    return null;
  };
}

onSubmit(): void {

	if (this.eventForm.valid) {
		const userId = window.localStorage.getItem("userId");
		const title = this.newEvent.name;
		const description = this.newEvent.description;
		const dateTimeLocal = this.newEvent.dateTime;
		const place = this.newEvent.address;
		const category = this.newEvent.category;

		if (dateTimeLocal) {
		const date = new Date(dateTimeLocal + 'Z');
		const formattedDateTime = date.toISOString().substring(0, 23);
		const endpoint = `/api/create-event/${userId}`;
		const formData = new FormData();

		for (const file of this.imageFiles) {
			formData.append('photos', file, file.name);
		}

		formData.append('title', title);
		formData.append('description', description);
		formData.append('dateTime', formattedDateTime);
		formData.append('place', place);
		formData.append('category', category);

		this.axiosService.requestMultipart(
			"POST",
			endpoint,
			formData,
			).then(response => {
        console.log("redirecting to event-board...");
        if (response.data === "Evento creato con successo") {
          this.router.navigate(["/event-board"])
        } else {
          console.log(response.data);
        }
      });
			console.log('Form submitted!');
		} else {
			console.error('Invalid date and time.');
		}
	}
}

  onFileChange(event: any): void {
    this.imageFiles = event.target.files;
  }
}


