import { Component } from '@angular/core';
import { LoginService } from '../services/login.service';
import { UserDto } from '../dtos/user.dto';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { CourseService } from '../services/courses.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  //styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  user: UserDto;
  formData: any = {
    firstName: '',
    lastName: '',
    email: '',
    topic: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
    imageFile: null
  };

  isEditing = false;

  taughtCourses: any[] = [];
  taughtLoading = false;

  constructor(
      public loginService: LoginService,
      public userService: UserService,
      private router: Router,
      private courseService: CourseService,
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

  onSubmit(){
    if(this.user){
      const userDTO = {
        id: this.user.id,
        firstName: this.formData.firstName,
        lastName: this.formData.lastName,
        email: this.formData.email,
        topic: this.formData.topic,
        roles: this.user.roles}

      this.userService.updateUser(userDTO)
        .subscribe(
          response => {
            console.log('User updated successfully', response);
            this.user = response; // Actualiza el usuario con la respuesta del servidor
          },
          error => {
            console.error('Error updating user', error);
          }
        );
    }
  }

  deleteAccount(){
    if(this.user){
      this.userService.deleteAccount(this.user.id).subscribe();
      this.loginService.logOut();
      this.router.navigate(['/courses']);
    }
  }

  public ngOnInit() {
    this.loadTaughtCourses();
  }

  public loadTaughtCourses(): void {
    this.taughtLoading = true;
    this.courseService.getTaughtCourses().subscribe({
      next: (courses) => {
        this.taughtCourses= courses;
        this.taughtLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar cursos impartidos:', err);
      }
      
    });
  }
}