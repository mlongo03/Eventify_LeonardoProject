import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { RedirectService } from '../redirect.service';
import { AxiosService } from '../axios.service';

@Component({
  selector: 'app-filter-form',
  templateUrl: './filter-form.component.html',
  styleUrls: ['./filter-form.component.css'],
})
export class FilterFormComponent implements OnInit {
  categoria = new FormControl([]);
  categoriaList: string[] = [
    'PARTY', 'GYM', 'ROLEGAME', 'SPORT', 'MEETING', 'CONFERENCE',
    'NETWORKING', 'HOBBY', 'MUSIC', 'BUSINESS', 'FOOD', 'NIGHTLIFE',
    'HEALTH', 'HOLIDAYS'
  ];

  filtro: any = {
    Titolo: '',
    Luogo: '',
    startDate: null,
    endDate: null,
    Categoria: ['EMPTY'],
    typeEventPage: this.redirectService.getRedirect()
  };

  private redirectTo: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private redirectService: RedirectService,
    private axiosService: AxiosService
  ) {
    this.redirectTo = redirectService.getRedirect();
  }

  ngOnInit(): void {
    this.axiosService.authenticate();
  }

  applicaFiltro() {
    this.router.navigate(['/event-board'], {
      relativeTo: this.route,
      queryParams: { filtro: JSON.stringify(this.filtro) }
    });
  }
}

