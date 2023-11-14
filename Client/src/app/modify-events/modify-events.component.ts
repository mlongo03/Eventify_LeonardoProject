import { Component } from '@angular/core';
import { AxiosService } from '../axios.service';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AxiosResponse } from 'axios';

@Component({
  selector: 'app-modify-events',
  templateUrl: './modify-events.component.html',
  styleUrls: ['./modify-events.component.css']
})
export class ModifyEventsComponent {
  newEvent: any = {
    name: '',
    description: '',
    address: '',
    dateTime: '',
    category: '',
	image: ''
  };
  imageFiles: File[] = [];
  fieldFocused: boolean = false;
  fieldTouched: boolean = false;
  fieldFocused2: boolean = false;
  fieldTouched2: boolean = false;
  fieldFocused3: boolean = false;
  fieldTouched3: boolean = false;
  eventForm: FormGroup;
  customPattern = /.*?,.*?,.*\d.*\d.*\d.*\d.*\d./;

  constructor(private axiosService: AxiosService, private formBuilder: FormBuilder, private route: ActivatedRoute) {
    this.eventForm = this.formBuilder.group({
      name: [this.newEvent.name, [Validators.required]],
      description: [this.newEvent.description, []],
      place: [this.newEvent.address, [Validators.required, Validators.pattern(this.customPattern)]],
      dateTime: [this.newEvent.dateTime, [Validators.required, this.DateRangeValidator()]],
      photo: ['', [Validators.required]],
      category: [this.newEvent.category, [Validators.required]]
    });
  }


  event: any;

  ngOnInit(): void {
    this.axiosService.authenticate();

    if (!this.event) {
      // Se l'evento non è stato fornito, crea un evento di esempio
      this.event = {
        id: '',
        title:'',
        category: '',
        description: '',
        address: '',
        date: new Date().toISOString(),
        // image:
      };
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.axiosService.request("GET", `/api/event/findById/${id}`, {})
        .then(response => {
          console.log("OK");
          this.event = response.data;
          console.log(this.event);
          const imageURLs = this.event.imageURL;
          const imageURLPromises = imageURLs.map((url: string) => this.loadURL(url));
          Promise.all(imageURLPromises)
            .then(urls => {
              this.event.imageURL = urls;
            })
            .catch(error => {
              console.error('Error loading image URLs:', error);
            });
          }).catch(error => {})
        })
     }
    }

    loadURL(endpoint: string): Promise<string> {
      return new Promise((resolve, reject) => {
        this.axiosService.customGet(endpoint)
          .subscribe((response: AxiosResponse) => {
            const reader = new FileReader();
            reader.onload = () => {
              resolve(reader.result as string);
            };
            reader.readAsDataURL(response.data);
          }, (error) => {
            console.error('Error retrieving the image:', error);
            reject(error);
          });
      });
    }

  DateRangeValidator(): Validators {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (control.value) {
        const selectedDate = new Date(control.value);
        const currentDate = new Date();
        const maxDate = new Date();
        maxDate.setFullYear(maxDate.getFullYear() + 1);

        if (selectedDate < currentDate || selectedDate > maxDate) {
          return { dateRange: true };
        }
      }
      return null;
    };
  }

  onSubmit(): void {
    if (this.eventForm.valid) {
      this.route.params.subscribe(params => {
        const eventId = +params['id'];
        const userId = window.localStorage.getItem("userId");
        const title = this.newEvent.name;
        const description = this.newEvent.description;
        const dateTimeLocal = this.newEvent.dateTime;
        const place = this.newEvent.location;
        const category = this.newEvent.category;

        if (dateTimeLocal) {
          const date = new Date(dateTimeLocal + 'Z');
          const formattedDateTime = date.toISOString().substring(0, 23);
          const endpoint = `/api/event/${eventId}/modify-event/${userId}`;
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
            "PUT",
            endpoint,
            formData
          );
          console.log('Form submitted!');
        } else {
          console.error('Invalid date and time.');
        }
      })
    }
  }

  onFileChange(event: any): void {
    this.imageFiles = event.target.files;
  }

  removeImage(index: number): void {
    this.imageFiles.splice(index, 1);
}
}
