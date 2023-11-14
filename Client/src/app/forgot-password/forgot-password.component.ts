import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AxiosService } from '../axios.service';
import { Router } from '@angular/router';

@Component({
	selector: 'app-forgot-password',
	templateUrl: './forgot-password.component.html',
	styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
	emailForm: FormGroup;
	showError = false;
	errorMessage = '';

	constructor(private formBuilder: FormBuilder, private axiosService: AxiosService, private router: Router) {
		this.emailForm = this.formBuilder.group({
			email: [
				'',
				[
					Validators.required,
					Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)
				]
			]
		});
	}

	ResetPassword() {
		this.router.navigate(['/reset-password']);
	}

	onSubmit() {
		console.log("submitted");
		if (this.emailForm.valid) {
		console.log("submitted");
			const email = this.emailForm.get('email')?.value
			this.axiosService.request2(
				"POST",
				"/api/auth/forgot-password",
				{
				email: email
				}
				).then(response => {
					if (response.data === "Email not found") {
						this.showError = true;
						this.errorMessage = response.data;
					} else {
						this.showError = false;
						this.errorMessage = '';
						window.localStorage.setItem("emailForgotPassword", response.data)
						this.router.navigate(['/reset-password']);
					}
				  })
				  .catch(error => {
					console.error('An error occurred during login:', error);
				  })

		}
	}
}
