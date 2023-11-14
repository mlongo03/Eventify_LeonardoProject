import { Component, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AxiosService } from '../axios.service';

@Component({
	selector: 'app-reset-password',
	templateUrl: './reset-password.component.html',
	styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
	resetForm: FormGroup;
	showError = false;
	errorMessage = '';

	constructor(private formBuilder: FormBuilder, private el: ElementRef, private router: Router, private axiosService: AxiosService) {
		this.resetForm = this.formBuilder.group({
			otp: [
				'',
				[
					Validators.required,
					Validators.pattern(/^\d{4,6}$/)
				]
			],
			newPassword: [
				'',
				[
					Validators.required,
					Validators.minLength(8),
					Validators.pattern(/^(?=.*[!@#$%^&*()_+[\]{}|~`<>?,.:;'"\\])(?=.*\d)(?=.*[A-Z]).*$/)
				]
			],
			confirmPassword: ['', Validators.required]
		});
	}

	isNewPasswordVisible = false;
	isConfirmPasswordVisible = false;

	toggleNewPasswordVisibility() {
		const input = this.el.nativeElement.querySelector('#password') as HTMLInputElement;
		  this.isNewPasswordVisible = !this.isNewPasswordVisible;
		  input.type = this.isNewPasswordVisible ? 'text' : 'password';
		}

	toggleConfirmPasswordVisibility() {
		const input = this.el.nativeElement.querySelector('#confirm-password') as HTMLInputElement;
		this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
		input.type = this.isConfirmPasswordVisible ? 'text' : 'password';
	}

	public emailSent: boolean = false;

	resendEmail() {
		this.axiosService.request2(
			"POST",
			"/api/auth/refresh-2FA",
			{
			email: window.localStorage.getItem("emailForgotPassword")
			})
		this.emailSent = true;
	}

	onSubmit() {
		console.log("submitted");

		if (this.resetForm.valid) {
			const otpValue = this.resetForm.get('otp')?.value;
			const newPasswordValue = this.resetForm.get('newPassword')?.value;
			const confirmPasswordValue = this.resetForm.get('confirmPassword')?.value;

			if (newPasswordValue === confirmPasswordValue)
			{
				this.axiosService.request2(
					"POST",
					"/api/auth/reset-password",
					{
					otp: otpValue,
					password: newPasswordValue,
					confirmPassword: confirmPasswordValue
					}
					).then(response => {
						if (response.data !== "Reset password done correctly") {
							this.showError = true;
							this.errorMessage = response.data;
						} else {
							this.showError = false;
							this.errorMessage = '';
							this.router.navigate(['/login']);
						}
					  })
					  .catch(error => {
						console.error('An error occurred during login:', error);
					  })
			} else {
			}
		}
	}
}
