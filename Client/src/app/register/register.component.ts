import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AxiosService } from '../axios.service';
import { Router } from '@angular/router';
import axios from 'axios';

@Component({
	selector: 'app-register',
	templateUrl: './register.component.html',
	styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
	showError = false;
	errorMessage = '';
	isPasswordVisible = false;
	isPassword2Visible = false;
	uploadedFileName = '';
	@ViewChild('fileInput') fileInput: any;
	registerForm: FormGroup;

	constructor(private el: ElementRef, private formBuilder: FormBuilder, private axiosService: AxiosService, private router: Router) {
		this.registerForm = this.formBuilder.group({
			firstName: [
				'',
				[
					Validators.required,
					Validators.pattern(/^[A-Za-z]+$/)
				]
			],
			lastName: [
				'',
				[
					Validators.required,
					Validators.pattern(/^[A-Za-z]+$/)
				]
			],
			dateOfBirth: [
				'',
				[
					Validators.required,
					this.ageValidator(18)
				]
			],
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
					Validators.minLength(8),
					Validators.pattern(/^(?=.*[!@#$%^&*()_+[\]{}|~`<>?,.:;'"\\])(?=.*\d)(?=.*[A-Z]).*$/)
				]
			],
			confirmPassword: [
				'',
				[
					Validators.required,
				]
			],
			photo: [
				'',
				[
					Validators.required,
				]
			],
			privacy: [
				false,
				[
					Validators.requiredTrue
				]
			],
		});
	}

	togglePasswordVisibility() {
	const input = this.el.nativeElement.querySelector('#password') as HTMLInputElement;
		this.isPasswordVisible = !this.isPasswordVisible;
		input.type = this.isPasswordVisible ? 'text' : 'password';
	}

	togglePasswordVisibility2() {
		const input = this.el.nativeElement.querySelector('#confirmPassword') as HTMLInputElement;
		this.isPassword2Visible = !this.isPassword2Visible;
		input.type = this.isPassword2Visible ? 'text' : 'password';
	}

	ageValidator(minAge: number) {
		return (control: FormGroup): { [key: string]: boolean } | null => {
			const birthDate = new Date(control.value);
			const today = new Date();
			const age = today.getFullYear() - birthDate.getFullYear();
			
			if (age < minAge && birthDate < today) {
				return { 'minAge': true };
			}

			return null;
		};
	};

	onSubmit() {
		const passwordValue = this.registerForm.get('password')?.value;
		const confirmPasswordValue = this.registerForm.get('confirmPassword')?.value;
		if (this.registerForm.valid && passwordValue === confirmPasswordValue) {
			const formData = new FormData();
			formData.append('firstName', this.registerForm?.get('firstName')?.value);
			formData.append('lastName', this.registerForm?.get('lastName')?.value);
			formData.append('dateOfBirth', this.registerForm?.get('dateOfBirth')?.value);
			formData.append('email', this.registerForm?.get('email')?.value);
			formData.append('password', this.registerForm?.get('password')?.value);
			formData.append('confirmPassword', this.registerForm?.get('confirmPassword')?.value);
			formData.append('checkbox', this.registerForm?.get('privacy')?.value);

			if (this.fileInput.nativeElement.files[0]) {
				formData.append('photo', this.fileInput.nativeElement.files[0]);
			} else {
				const emptyBlob = new Blob([], { type: 'application/octet-stream' });
				formData.append('photo', emptyBlob, 'empty.jpg');
			}

			if (this.registerForm?.get('dateOfBirth')?.value) {
				formData.append('dateOfBirth', this.registerForm?.get('dateOfBirth')?.value);
			} else {
				formData.append('dateOfBirth', '0000-00-00');
			}

			axios.post("/api/auth/signup", formData, {
				headers: {
				'Content-Type': 'multipart/form-data'
				},
				withCredentials: true,
			})
			.then(response => {
				if (response.data === "Registered Succesfully") {
				this.showError = false;
				this.errorMessage = '';
				this.router.navigate(['/login']);
				} else {
				this.showError = true;
				this.errorMessage = response.data;
				}
			})
			.catch(error => {
				this.showError = true;
				this.errorMessage = 'An unknown error occurred';
			});
		} else {
			// Dati inseriti errati
		}
	}
}
