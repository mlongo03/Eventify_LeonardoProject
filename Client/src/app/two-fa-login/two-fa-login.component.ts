import { Component, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AxiosService } from '../axios.service';
import { Router } from '@angular/router';
import { RedirectService } from '../redirect.service';

@Component({
  selector: 'app-two-fa-login',
  templateUrl: './two-fa-login.component.html',
  styleUrls: ['./two-fa-login.component.css']
})
export class TwoFALoginComponent {
	twoFAForm: FormGroup;
	showError = false;
	errorMessage = '';

	constructor(private formBuilder: FormBuilder, private el: ElementRef, private axiosService: AxiosService, private router: Router, private redirectService: RedirectService) {
		this.twoFAForm = this.formBuilder.group({
			otp: [
				'',
				[
					Validators.required,
					Validators.pattern(/^\d{4,6}$/)
				]
			]
		});
	}

	public emailSent: boolean = false;

	resendEmail() {
		console.log("resending email");
		this.axiosService.request2(
			"POST",
			"/api/auth/refresh-2FA",
			{
			email: window.localStorage.getItem("email")
			})
		this.emailSent = true;
	}

	onSubmit() {
		console.log("submitted otp");
		if (this.twoFAForm.valid) {
			console.log("submitted otp");
			const otpValue = this.twoFAForm.get('otp')?.value;
			this.axiosService.request2(
				"POST",
				"/api/auth/2FA",
				{
				otp: otpValue
				}
				).then(response => {
					if (response.data.error !== null) {
						this.showError = true;
						this.errorMessage = response.data.error;
					} else {
						this.showError = false;
						this.errorMessage = '';
						console.log(response.data.access_token);
						window.localStorage.setItem("expiration_date", response.data.expiration_date);
						window.localStorage.setItem("userId", response.data.userId);
						console.log(response.data.expiration_date);
						this.redirectService.setIsLogged(true);
						this.router.navigate(['/home']);
					}
				  })
				  .catch(error => {
					console.error('An error occurred during login:', error);
				  })
		} else {
			// newPassword e confirmPassword non corrispondono
		}
	}
}
