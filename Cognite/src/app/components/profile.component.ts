import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  //styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  user = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    topic: 'Desarrollo web'
  };

  passwords = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  isEditing = false;

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }

  onPhotoSelected(event: Event) {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files[0]) {
      const file = fileInput.files[0];
      console.log('Selected file:', file);
      // Aquí puedes manejar la subida del archivo
    }
  }

  onSubmit() {
    console.log('User data:', this.user);
    console.log('Passwords:', this.passwords);
    // Aquí puedes enviar los datos al backend
  }

  deleteAccount() {
    console.log('Deleting account for user ID:', this.user.id);
    // Aquí puedes manejar la lógica para eliminar la cuenta
  }
}