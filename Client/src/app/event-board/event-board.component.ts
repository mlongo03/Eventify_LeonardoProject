import { Component, OnInit } from '@angular/core';
import { AxiosService } from '../axios.service';
import { AxiosResponse } from 'axios';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { RedirectService } from '../redirect.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-event-board',
  templateUrl: './event-board.component.html',
  styleUrls: ['./event-board.component.css'],
})
export class EventBoardComponent implements OnInit {
  constructor(private axiosService: AxiosService, public dialog: MatDialog, private route: ActivatedRoute, private redirectService: RedirectService, private router: Router) {}
  showFilterForm = false;

  events: any[] = [ ];
  filtro: any;

  ngOnInit() {
	console.log("Requesting events...");

	this.axiosService.authenticate();
	this.route.queryParams.subscribe((params) => {
	  if (params['filtro']) {
		this.filtro = JSON.parse(params['filtro']);
		console.log(this.filtro);
		console.log(this.filtro.Titolo);
	  }
	this.loadDataBasedOnFiltro();
	});
	console.log(this.filtro);
  }

  loadDataBasedOnFiltro() {
	const filters = {
	  title: '',
	  place: '',
	  dateStart: '',
	  dateEnd: '',
	  category: ['EMPTY'],
	  typeEventPage: this.redirectService.getRedirect()
	};

	if (this.filtro) {
	  filters.title = this.filtro.Titolo || '';
	  filters.place = this.filtro.Luogo || '';
	  filters.dateStart = this.filtro.startDate || '';
	  filters.dateEnd = this.filtro.endDate || '';
	  filters.category = this.filtro.Categoria || [];
	  filters.typeEventPage = this.redirectService.getRedirect();
	}

	const userId = window.localStorage.getItem("userId");

	console.log("CATEGORIA : " + filters.category);
	console.log("eventPage : " + filters.typeEventPage);

	this.axiosService.request(
	  "POST",
	  `/filter/${userId}`,
	  filters
	).then(response => {
	  console.log("OK");
	  this.events = response.data;

	  const imageLoadingPromises = this.events.map(event => this.loadURL(event.imageURL));

	  Promise.all(imageLoadingPromises)
		.then(imageURLs => {
		  imageURLs.forEach((imageURL, index) => {
			this.events[index].imageURL = imageURL;
		  });

		  console.log(this.events);
		})
		.catch(error => {
		  console.error('Error loading images:', error);
		});
	})
	.catch(error => {
	  console.log("Error");
	});
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

  toggleFilterForm() {
	this.showFilterForm = !this.showFilterForm;
  }

  addFilter() {
	this.router.navigate(["/event-board-filters"]);
  }
}
