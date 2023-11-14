import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import { Observable } from 'rxjs';
import { RedirectService } from './redirect.service';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  constructor(private router: Router, private redirect: RedirectService) {
    axios.defaults.baseURL = "https://localhost:8443";
    axios.defaults.withCredentials = true; // Enable sending cookies with requests
    axios.defaults.headers.post["Content-Type"] = "application/json";
  }

  checkExpiration() {
    const expirationDate = window.localStorage.getItem("expiration_date");

    if (expirationDate !== null) {

      const expirationTimestamp = parseInt(expirationDate, 10);

      if (expirationTimestamp) {

        const currentDate = Date.now();


        if (currentDate > expirationTimestamp) {
          console.log("loading new access token...");
          this.request2(
            "POST",
            "/api/auth/refreshToken",
            {
            }
            ).then(response => {
              window.localStorage.setItem("expiration_date", response.data.expiration_date);
              console.log("loaded new access token");
            })
            .catch(error => {
              console.log("fail during refresh token");
              this.router.navigate(['/login']);
            })
        }
      }
    }
  }


  async request(method: string, url: string, data: any): Promise<any> {
    const expirationDate = window.localStorage.getItem("expiration_date");

    if (expirationDate !== null) {
      const expirationTimestamp = parseInt(expirationDate, 10);

      if (expirationTimestamp) {
        const currentDate = Date.now();

        if (currentDate > expirationTimestamp) {
          console.log("loading new access token...");
          try {
            const response = await this.request2("POST", "/api/auth/refreshToken", {});
            console.log("got new access token!");
            window.localStorage.setItem("expiration_date", response.data.expiration_date);
            console.log("loaded new access token");
          } catch (error) {
            this.logout();
          }
        }
      }
    }
    console.log("nice");
    const config: AxiosRequestConfig = {
      method: method,
      url: url,
      data: data,
    };

    return axios(config);
  }

  request2(method: string, url: string, data: any): Promise<any> {

    const config: AxiosRequestConfig = {
      method: method,
      url: url,
      data: data,
    };

    return axios(config);
  }

  async requestMultipart(method: string, url: string, data: any): Promise<any> {
    const expirationDate = window.localStorage.getItem("expiration_date");

    if (expirationDate !== null) {
      const expirationTimestamp = parseInt(expirationDate, 10);

      if (expirationTimestamp) {
        const currentDate = Date.now();

        if (currentDate > expirationTimestamp) {
          console.log("loading new access token...");
          try {
            const response = await this.request2("POST", "/api/auth/refreshToken", {});
            console.log("got new access token!");
            window.localStorage.setItem("expiration_date", response.data.expiration_date);
            console.log("loaded new access token");
          } catch (error) {
            this.logout();
          }
        }
      }
    }
    console.log("nice");
    const config: AxiosRequestConfig = {
      method: method,
      url: url,
      data: data,
      headers:  { 'Content-Type': 'multipart/form-data' }
    };

    return axios(config);
  }

  customGet(url: string): Observable<AxiosResponse> {
    const config: AxiosRequestConfig = {
      method: 'GET',
      url: url,
      responseType: 'blob', // Set the responseType to blob for binary data
    };

    return new Observable<AxiosResponse>((observer) => {
      axios(config)
        .then((response: AxiosResponse) => {
          observer.next(response);
          observer.complete();
        })
        .catch((error) => {
          observer.error(error);
        });
    });
  }

  logout() {
    this.request2("POST", "api/auth/logout", {})
      .then((response) => {
        this.router.navigate(['/login'])
      })
      .catch((error) => {
        console.error("Error logging out:", error);
      });
  }

  authenticate() {
      this.request('POST', 'api/authenticate', {})
      .then((response) => {
        this.redirect.setIsLogged(true);
      })
      .catch((error) => {
        this.redirect.setIsLogged(false);
        console.log('error');
        this.router.navigate(['/login']);
      });
  }
}
