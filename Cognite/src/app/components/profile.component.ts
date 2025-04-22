import { Component } from '@angular/core';
import { LoginService } from '../services/login.service';
import { UserDto } from '../dtos/user.dto';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { ReviewService } from '../services/reviews.service';
import { ReviewDto } from '../dtos/review.dto';
import { CourseDto } from '../dtos/course.dto';
import { EnrollmentService } from '../services/enrollment.service';

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
  isAdmin = false;
  editingReview: number | null = null;

  taughtCourses: CourseDto[] = [];
  taughtLoading = false;
  reviews: ReviewDto[] = [];
  users: UserDto[] = [];
  searchQuery = '';
  enrolledCourses: CourseDto[] = [];
  enrolledLoading = false;
  hasMoreTaughtCourses = true;
  currentPage = 0;
  pageSize = 10;
  currentPageEnrolled = 0;
  pageSizeEnrolled = 10;
  hasMoreEnrolledCourses = true;
  isEditing = false;
  constructor(
      public loginService: LoginService,
      public userService: UserService,
      private router: Router,
      private courseService: CourseService,
      public reviewService: ReviewService,
      private enrollmentService: EnrollmentService
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
      this.isAdmin = this.loginService.isAdmin();
      if (this.isAdmin) {
        this.reviewService.getPendingReviews().subscribe({
          next: (data) => this.reviews = data,
          error: (err) => console.error('Error al cargar reseñas pendientes:', err)
        });
        
      } else {
        this.loadTaughtCourses();
        this.loadEnrolledCourses();
      }
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

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }

  public loadTaughtCourses(): void {
    this.taughtLoading = true;
    this.courseService.getTaughtCourses(this.user.id, this.currentPage, this.pageSize).subscribe({
      next: (courses) => {
        if (courses.length === 0) {
          this.hasMoreTaughtCourses = false;
        } else {
          this.taughtCourses = [...this.taughtCourses, ...courses];
          this.currentPage++;
        }
        this.taughtLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar cursos:', err);
        this.taughtLoading = false;
      }
    });
  }

  public loadMoreTaught(): void {
    this.loadTaughtCourses();
  }

  public loadEnrolledCourses(): void {
    this.enrolledLoading = true;
    this.enrollmentService.getEnrolledCourses(this.user.id, this.currentPageEnrolled, this.pageSizeEnrolled).subscribe({
      next: (courses) => {
        if (courses.length === 0) {
          this.hasMoreEnrolledCourses = false;
        } else {
          this.enrolledCourses = [...this.enrolledCourses, ...courses];
          this.currentPageEnrolled++;
        }
        this.enrolledLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar cursos:', err);
        this.enrolledLoading = false;
      }
    });
  }

  public loadMoreEnrolled(): void {
    this.loadEnrolledCourses();
  }

  loadPendingReviews(): void {
    this.reviewService.getPendingReviews().subscribe({
      next: (data) => this.reviews = data,
      error: (err) => console.error('Error al cargar reseñas pendientes:', err)
    });
  }

  deleteReview(id: number ): void {
    this.reviewService.deleteReview(id).subscribe({
      next: () => {
        console.log('Reseña eliminada');
        this.reviews = this.reviews.filter(r => r.id !== id);
      },
      error: (err) => console.error('Error al eliminar reseña:', err)
    });
  }

  ignoreReview(id: number): void {
    this.reviewService.ignoreReview(id).subscribe({
      next: () => {
        console.log('Reseña ignorada');
        this.reviews = this.reviews.filter(r => r.id !== id);
      },
      error: (err) => console.error('Error al ignorar reseña:', err)
    });
  }

  deleteUser(id: number): void {
    this.userService.deleteAccount(+id).subscribe({
      next: () => {
        console.log('Usuario eliminado');
        this.users = this.users.filter(u => u.id !== id);
      },
      error: (err) => console.error('Error al eliminar usuario:', err)
    });
  }

  editReview(review: ReviewDto): void {
    this.editingReview = review.id;
  }

  cancelEdit(): void {
    this.editingReview = null;
  }

  submitReviewEdit(review: ReviewDto): void {
    this.reviewService.editReview(review).subscribe({
      next: () => {
        this.editingReview = null;
      },
      error: (err) => console.error('Error al actualizar la reseña:', err)
    });
  }

  loadAllUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Error al cargar usuarios:', err)
    });
  }


  searchUser(): void {
    this.userService.getUsers(this.searchQuery).subscribe({
      next: users => this.users = users,
      error: err => console.error('Error al buscar usuarios', err)
    });
  }
}