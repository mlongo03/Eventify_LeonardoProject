import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AxiosService } from '../axios.service';
import { AxiosResponse } from 'axios';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{
	profileForm: FormGroup;

	constructor(private formBuilder: FormBuilder, private router: Router, private axiosService: AxiosService) {
		this.profileForm = this.formBuilder.group({
			firstname: [
				'',
				[
					Validators.required,
					Validators.pattern(/^[A-Za-z]+$/)
				]
			],
			lastname: [
				'',
				[
					Validators.required,
					Validators.pattern(/^[A-Za-z]+$/)
				]
			],
			email: [
				'',
				[
					Validators.required,
					Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)
				],
			]
		});
	}

	firstname: string = '';
	lastname: string = '';
	oldEmail: string = '';
	newEmail: string = '';
	newFirstname: string = '';
	newLastname: string = '';
	date: string = '';
	profilePhoto: string = '';
	returnProfilePhoto: File | undefined;
	returnFirstname: string = '';
	returnLastname: string = '';
	returnEmail: string = '';

	ngOnInit(): void {

		const userId = window.localStorage.getItem("userId");

		this.axiosService.request(
			"GET",
			`/api/getProfileInfo/${userId}`,
			{}
		).then(response => {
			this.firstname = response.data.firstName;
			this.newFirstname = response.data.firstName;
			this.lastname = response.data.lastName;
			this.newLastname = response.data.lastName;
			this.oldEmail = response.data.email;
			this.newEmail = response.data.email;
			this.date = response.data.date;
			this.loadURL(response.data.imageUrl)
			.then((result) => {
				this.profilePhoto = result;
			})
			.catch((error) => {
				console.error('Error loading profile photo:', error);
			});
		})
		throw new Error('Method not implemented.');
	}



	modifyPermit: boolean = false;
	showButton: boolean = true;

	ModifyProfile() {
		this.modifyPermit = true;
		this.showButton = false;
	}

	EmailChange(tmp: string) {
		this.newEmail = tmp;
	}

	firstNameChange(tmp: string) {
		this.newFirstname = tmp;
		console.log(this.newFirstname);
	}

	lastNameChange(tmp: string) {
		this.newLastname = tmp;
	}

	SaveChanges() {
		console.log(this.newFirstname + ", " + this.firstname);
		const userId = window.localStorage.getItem("userId");
		if (this.newEmail !== this.oldEmail) {
			this.returnEmail = this.newEmail;
		}
		if (this.newFirstname !== this.firstname) {
			this.returnFirstname = this.firstname;
		}
		if (this.newLastname !== this.lastname) {
			this.returnLastname = this.lastname;
		}
		if (this.returnProfilePhoto === undefined) {
			//chiamata non multipart
			const endpoint = `/api/modify-profile-multipart/${userId}`;
			this.axiosService.requestMultipart("PUT", endpoint,
			{
				firstname: this.returnFirstname,
				lastname: this.returnLastname,
				email: this.returnEmail,
				profilePhoto: this.returnProfilePhoto,
			}).then(response => {
				if (response.data === "Profile succesfully updated.") {
					window.location.reload();
				} else if (response.data === "Email isn't let validated.") {
					this.router.navigate(['/2FA-login']);
				} else {
					//mostra errore scritto in response.data
				}
			});
		} else {
			const endpoint = `/api/modify-profile/${userId}`;
			this.axiosService.request("PUT", endpoint,
			{
				firstname: this.returnFirstname,
				lastname: this.returnLastname,
				email: this.returnEmail,
				profilePhoto: this.returnProfilePhoto,
			}).then(response => {
				if (response.data === "Profile succesfully updated.") {
					window.location.reload();
				} else if (response.data === "Email isn't let validated.") {
					this.router.navigate(['/2FA-login']);
				} else {
					//mostra errore scritto in response.data
				}
			});
		}
	}

	ReloadPage() {
		window.location.reload();
	}

	UploadNewPhoto(event: any) {
		this.returnProfilePhoto = event.target.files[0]; // Ottieni il file selezionato dall'input
		// if (file) {
		// 	const reader = new FileReader();
		// 	reader.onload = (e: any) => {
		// 		this.returnProfilePhoto = e.target.result; // Imposta l'immagine visualizzata sulla pagina con i dati del file caricato
		// 	};
		// 	reader.readAsDataURL(file);
		// }
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
}
