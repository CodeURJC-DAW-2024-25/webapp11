import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../services/user.service';
import { LoginService } from '../services/login.service';
import { UserDto } from '../dtos/user.dto';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
})
export class AdminComponent implements OnInit {
  admin: UserDto | null = null;
  profileImageUrl = '';
  reviews: any[] = [];
  users: any[] = [];
  showEdit = false;
  editingReview: string | null = null;
  searchQuery = '';

  editForm!: FormGroup;
  topics = [
    'Desarrollo web', 'Desarrollo móvil', 'Desarrollo de videojuegos', 'Emprendimiento',
    'Finanzas', 'Marketing digital', 'Liderazgo', 'Comunicación'
  ];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.setupForm();
    this.fetchAdminData();
    this.fetchReviews();
    this.fetchUsers();
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

  fetchReviews(): void {
    // Lógica para obtener reviews pendientes (con tu ReviewService)
    // this.reviewService.getPendingReviews().subscribe(data => this.reviews = data);
  }

  fetchUsers(): void {
    // Lógica para obtener usuarios
    // this.userService.getAllUsers().subscribe(data => this.users = data);
  }

  toggleEdit(): void {
    this.showEdit = !this.showEdit;
    if (this.showEdit && this.admin) {
      this.editForm.patchValue({
        firstName: this.admin.firstName,
        lastName: this.admin.lastName,
        email: this.admin.email,
        topic: this.admin.topic
      });
    }
  }

  saveChanges(): void {
    if (!this.admin || this.passwordMismatch) return;

    const updatedData = {
      id: this.admin.id,
      ...this.editForm.value
    };

    this.userService.updateUser(updatedData).subscribe({
      next: (updatedUser) => {
        this.admin = updatedUser;
        this.showEdit = false;
        this.editForm.patchValue({
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        });
        console.log('Perfil actualizado correctamente');
      },
      error: (err) => console.error('Error al actualizar perfil:', err)
    });

    const { currentPassword, newPassword } = this.editForm.value;
    if (currentPassword && newPassword) {
      this.userService.changePassword(currentPassword, newPassword).subscribe({
        next: () => console.log('Contraseña cambiada correctamente'),
        error: (err) => console.error('Error al cambiar contraseña:', err)
      });
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

  editReview(review: any): void {
    this.editingReview = review.id;
  }

  cancelEdit(): void {
    this.editingReview = null;
  }

  submitReviewEdit(review: any): void {
    // this.reviewService.editReview(review.id, review.text).subscribe(() => {
    //   this.editingReview = null;
    // });
  }

  deleteReview(id: string): void {
    // this.reviewService.deleteReview(id).subscribe(() => {
    //   this.reviews = this.reviews.filter(r => r.id !== id);
    // });
  }

  ignoreReview(id: string): void {
    // this.reviewService.ignoreReview(id).subscribe(() => {
    //   this.reviews = this.reviews.filter(r => r.id !== id);
    // });
  }

  deleteUser(id: string): void {
    // this.userService.deleteUser(id).subscribe(() => {
    //   this.users = this.users.filter(u => u.id !== id);
    // });
  }

  searchUser(): void {
    // this.userService.searchUsers(this.searchQuery).subscribe(data => this.users = data);
  }
}
