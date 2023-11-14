import { Injectable, OnInit } from '@angular/core';
import { AxiosService } from './axios.service';

@Injectable({
  providedIn: 'root'
})
export class RedirectService{
	private redirectTo: string = '/event-board';
  
  isLogged: boolean = false;

  constructor() { }

  setRedirect(route:string){
  	this.redirectTo = route;
  }

  getRedirect(): string {
	  return this.redirectTo;
  }

  setIsLogged(bool: boolean) {
		this.isLogged = bool;
	}

  getIsLogged(): boolean {
    return this.isLogged;
  }


}


