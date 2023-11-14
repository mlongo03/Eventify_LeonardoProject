import { Component, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AxiosService } from '../axios.service';
import { Router } from '@angular/router';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css'],
})
export class LoginComponent {

	showError = false;
	errorMessage = '';
	isPasswordVisible = false;
	loginForm: FormGroup;

	constructor(private formBuilder: FormBuilder, private el: ElementRef, private axiosService: AxiosService, private router: Router){
		this.loginForm = this.formBuilder.group({
			email: [
				'',
				[
					Validators.required,
					Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)
				]
			],
			password: [
				'',
				[
					Validators.required,
				]
			],
		})
	}

	togglePasswordVisibility() {
		const input = this.el.nativeElement.querySelector('#pwd') as HTMLInputElement;
		this.isPasswordVisible = !this.isPasswordVisible;
		input.type = this.isPasswordVisible ? 'text' : 'password';
	}

	PasswordForgotten() {
		this.router.navigate(['/forgot-password']);
	}

	Register() {
		this.router.navigate(['/register']);
	}

	onSubmit() {
		console.log("submitted");
		if (this.loginForm.valid) {
			console.log("sending login request");
			this.axiosService.request2(
				"POST",
				"/api/auth/signin",
				{
				email: this.loginForm.get('email')?.value,
				password: this.loginForm.get('password')?.value
				}
				).then(response => {
					if (response.data.error !== null) {
						this.showError = true;
						this.errorMessage = response.data.error;
						this.axiosService.request2(
							"POST",
							"/api/auth/signin-failure",
							{
								email: this.loginForm.get('email')?.value
							})
					} else {
						console.log(response.data.email);
						window.localStorage.setItem("email", response.data.email);
						this.router.navigate(['/2FA-login']);
					}
				  })
				  .catch(error => {
					this.showError = true;
					this.errorMessage = 'An error occurred during login:', error;
					console.error('An error occurred during login:', error);
				  })

		} else {
			// Dati inseriti errati
		}
	}
}
