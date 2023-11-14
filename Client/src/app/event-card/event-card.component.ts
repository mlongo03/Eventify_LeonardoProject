import { Component, ElementRef, AfterViewInit, Renderer2, Input, OnInit } from '@angular/core';
import { AxiosService } from '../axios.service';
import { AxiosResponse } from 'axios';



type CategoryColors = {
	[key: string]: string;
  };


@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.css']
})
export class EventCardComponent implements AfterViewInit, OnInit {
  @Input() event: any;

  categoryColors: CategoryColors = {
    PARTY: '#ff0000', // Rosso
    GYM: '#ff8000' ,// Arancio
    ROLEGAME: '#653239', // Giallo
    SPORT: '#27AE60', // Verde
    MEETING: '#083D77', // Blu
    CONFERENCE: '#FFBA08', // Ciano
    NETWORKING: '#7f5539', // Giallo
    HOBBY: '#f032e6', // Magenta
    MUSIC: '#fabed4', // Pink
    BUSINESS: '#34495E', // Grigio scuro
    FOOD: '#911eb4', // Viola
    NIGHTLIFE: 'black', // Arancione
    HEALTH: '#27AE60', // Verde
    HOLIDAYS: '#3498DB' // Blu
  };

  constructor(private elementRef: ElementRef, private renderer: Renderer2, private axiosService: AxiosService) {}



  ngOnInit() {
    if (!this.event) {
      // Se l'evento non è stato fornito, crea un evento di esempio
      this.event = {
		id: '',
        title: '',
        category: '',
        description: '',
        address: '',
        date: new Date().toISOString(),
        // image:
      };
    }
  }

  ngAfterViewInit() {
    // Ottieni l'elemento con width: auto; (l'elemento HTML del componente)
    const element = this.elementRef.nativeElement;

    // Calcola la larghezza effettiva in pixel
    const width = element.offsetWidth;

    // Puoi fare qualcosa con la larghezza, ad esempio, stamparla nella console
    console.log(`Larghezza effettiva: ${width}px`);
  }
}
