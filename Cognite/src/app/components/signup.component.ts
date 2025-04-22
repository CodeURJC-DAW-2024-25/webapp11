import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SignupService } from '../services/signup.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup;
  selectedFile!: File;

  constructor(private fb: FormBuilder, private signupService: SignupService) {
    this.signupForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      topic: ['', Validators.required],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required]
    });
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit() {
    if (this.signupForm.invalid || !this.selectedFile) return;

    const formData = new FormData();
    Object.entries(this.signupForm.value).forEach(([key, value]) => {
      formData.append(key, value as string);
    });
    formData.append('profileImage', this.selectedFile);

    this.signupService.registerUser(formData).subscribe({
      next: () => alert('Usuario registrado correctamente'),
      error: (err: any) => console.error(err)
    });
  }
}
