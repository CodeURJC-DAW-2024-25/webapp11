import { Component } from '@angular/core';
import { LoginService } from '../services/login.service';
import { UserDto } from '../dtos/user.dto';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  //styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  user: UserDto;

  passwords = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  isEditing = false;

  constructor(
      public loginService: LoginService
    ) {
      let user1 = this.loginService.currentUser()
      if (user1) {
        this.user = user1;
      } else{
        this.user={
          id: -1,
          firstName: '',
          lastName: '',
          email: '',
          topic: '',
          roles: ['USER'],
        };
      }
    }

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