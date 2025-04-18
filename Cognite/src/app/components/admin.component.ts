import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../services/user.service';
import { LoginService } from '../services/login.service';
import { UserDto } from '../dtos/user.dto';
import { Router } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { ReviewService } from '../services/reviews.service';
import { ReviewDto } from '../dtos/review.dto';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
})
export class AdminComponent implements OnInit {
  admin: UserDto | null = null;
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
  reviews: ReviewDto[] = [];
  users: UserDto[] = [];
  searchQuery = '';
  editForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private loginService: LoginService,
    private router: Router,
    private courseService: CourseService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.setupForm();
    this.fetchAdminData();
    this.loadTaughtCourses();
    this.loadAllUsers();
    this.loadPendingReviews();
  }

  setupForm(): void {
    this.editForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      email: [''],
      topic: [''],
      currentPassword: [''],
      newPassword: [''],
      confirmPassword: ['']
    });
  }

  get passwordMismatch(): boolean {
    const pwd = this.editForm.get('newPassword')?.value;
    const confirm = this.editForm.get('confirmPassword')?.value;
    return pwd && confirm && pwd !== confirm;
  }

  fetchAdminData(): void {
    this.userService.getUserInfo().subscribe({
      next: (user) => {
        this.admin = user;
        this.editForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          topic: user.topic,
        });
      },
      error: (err) => console.error('Error al obtener datos del admin:', err)
    });
  }

  loadTaughtCourses(): void {
    this.taughtLoading = true;
    this.courseService.getTaughtCourses().subscribe({
      next: (courses) => {
        this.taughtCourses = courses;
        this.taughtLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar cursos impartidos:', err);
        this.taughtLoading = false;
      }
    });
  }

  loadAllUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Error al cargar usuarios:', err)
    });
  }

  loadPendingReviews(): void {
    this.reviewService.getPendingReviews().subscribe({
      next: (data) => this.reviews = data,
      error: (err) => console.error('Error al cargar reseñas pendientes:', err)
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (this.isEditing && this.admin) {
      this.formData.firstName = this.admin.firstName;
      this.formData.lastName = this.admin.lastName;
      this.formData.email = this.admin.email;
      this.formData.topic = this.admin.topic;
    }
  }

  onPhotoSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.userService.uploadProfilePhoto(file).subscribe({
        next: () => console.log('Foto subida con éxito'),
        error: (err) => console.error('Error al subir foto:', err)
      });
    }
  }

  onSubmit(): void {
    if (!this.admin || this.passwordMismatch) return;

    const updatedData = {
      id: this.admin.id,
      firstName: this.formData.firstName,
      lastName: this.formData.lastName,
      email: this.formData.email,
      topic: this.formData.topic,
      roles: this.admin.roles
    };

    this.userService.updateUser(updatedData).subscribe({
      next: (updatedUser) => {
        this.admin = updatedUser;
        this.isEditing = false;
        console.log('Perfil actualizado correctamente');
      },
      error: (err) => console.error('Error al actualizar perfil:', err)
    });

    const { currentPassword, newPassword } = this.formData;
    if (currentPassword && newPassword) {
      this.userService.changePassword(currentPassword, newPassword).subscribe({
        next: () => console.log('Contraseña cambiada correctamente'),
        error: (err) => console.error('Error al cambiar contraseña:', err)
      });
    }
  }

  deleteAccount(): void {
    if (this.admin) {
      this.userService.deleteAccount(this.admin.id).subscribe(() => {
        this.loginService.logOut();
        this.router.navigate(['/courses']);
      });
    }
  }

  deleteReview(id: string): void {
    this.reviewService.deleteReview(id).subscribe({
      next: () => {
        console.log('Reseña eliminada');
        this.reviews = this.reviews.filter(r => r.id !== Number(id));
      },
      error: (err) => console.error('Error al eliminar reseña:', err)
    });
  }

  ignoreReview(id: string): void {
    this.reviewService.ignoreReview(id).subscribe({
      next: () => {
        console.log('Reseña ignorada');
        this.reviews = this.reviews.filter(r => r.id !== Number(id));
      },
      error: (err) => console.error('Error al ignorar reseña:', err)
    });
  }

  deleteUser(id: string): void {
    this.userService.deleteAccount(+id).subscribe({
      next: () => {
        console.log('Usuario eliminado');
        this.users = this.users.filter(u => u.id !== +id);
      },
      error: (err) => console.error('Error al eliminar usuario:', err)
    });
  }

  searchUser(): void {
    const query = this.searchQuery.toLowerCase().trim();
    if (query) {
      this.users = this.users.filter(user =>
        user.firstName.toLowerCase().includes(query) ||
        user.lastName.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query)
      );
    } else {
      this.loadAllUsers();
    }
  }
}
