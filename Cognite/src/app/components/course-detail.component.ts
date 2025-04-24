import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';
import { ReviewDto } from '../dtos/review.dto';
import { ReviewService } from '../services/reviews.service';
import { EnrollmentService } from '../services/enrollment.service';
import { HttpClient } from '@angular/common/http';
import { PDFDocumentProxy } from 'ng2-pdf-viewer';
import { UserService } from '../services/user.service';
@Component({
  templateUrl: './course-detail.component.html',
  styleUrls: ["../app.component.css"],
})
export class CourseDetailComponent {
  public course!: CourseDto;
  reviews: ReviewDto[] = [];
  active = 1;
  pdfSrc: any;
  zoom: number = 1;
  page: number = 1;
  totalPages: number = 0;
  comentarioTexto: string = '';
  comentarios: { [reviewId: number]: string } = {};
  public isEnrolled: boolean = false;
  public isInstructor: boolean = false;
  selectedRating: number = 0;
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private loginService: LoginService,
    private reviewService: ReviewService,
    private enrollmentService: EnrollmentService,
    private http: HttpClient,
    private userService: UserService
  ) {
    const courseId = this.route.snapshot.params['id'];
    this.loadCourseDetails(courseId);
    this.loadReviews(courseId);
    this.loadPdf(courseId);
    this.checkEnrollmentStatus();
    this.checkInstructorStatus(); 
    this.loginService.loggedIn$.subscribe((loggedIn) => {
      if (loggedIn) {
        console.log("Usuario ha iniciado sesión, refrescando datos del curso");
        this.loadCourseDetails(courseId);
        this.checkEnrollmentStatus();
        this.checkInstructorStatus();
        this.loadReviews(courseId);
        this.loadPdf(courseId);
      }
    });
  }

  private loadCourseDetails(courseId: number) {
    this.courseService.getCourseById(courseId).subscribe(
      (course) => (this.course = course),
      (error) => console.error(error)
    );
  }

  private loadReviews(courseId: number) {
    this.reviewService.getReviewsByCourse(courseId).subscribe(
      (reviews) => (this.reviews = reviews),
      (error) => console.error(error)
    );
  }

  public enrollCourse(): void {
    const courseId = this.route.snapshot.params['id'];
    this.userService.getUserInfo().subscribe(
      (user) => {
        const userId = user.id;
        this.enrollmentService.enrollCourse(userId, courseId).subscribe(
          (response) => {
            console.log("Usuario inscrito en el curso con éxito", response);
            this.isEnrolled = true;
          },
          (error) => console.error("Error al inscribir al usuario:", error)
        );
      },
      (error) => console.error("Error al obtener el usuario:", error)
    );
  }
  
  public submitRating(): void {
    const rating = this.selectedRating;
    this.userService.getUserInfo().subscribe(
      (user) => {
        const userId = user.id;
        const courseId = this.route.snapshot.params['id'];
        this.enrollmentService.isUserEnrolled(userId, courseId).subscribe(
          (enrolled) => {
            if (enrolled) {
                this.enrollmentService.rateCourse(enrolled, rating).subscribe();
            } else {
              console.log("User is not enrolled in the course");
            }
            if (enrolled) {
              console.log("User is enrolled in the course");
            } else {
              console.log("User is not enrolled in the course");
            }
          },
          (error) => console.error("Error checking enrollment:", error)
        );
      },
      (error) => console.error("Error getting user info:", error)
    );
  }


  public checkEnrollmentStatus(): void {
    this.userService.getUserInfo().subscribe(
      (user) => {
        const userId = user.id;
        const courseId = this.route.snapshot.params['id'];
        this.enrollmentService.isUserEnrolled(userId, courseId).subscribe(
          (enrolled) => {
            if (enrolled) {
              this.isEnrolled = true;
              console.log("User is enrolled in the course");
            } else {
              this.isEnrolled = false;
              console.log("User is not enrolled in the course");
            }
          },
          (error) => console.error("Error checking enrollment:", error)
        );
      },
      (error) => console.error("Error getting user info:", error)
    );
  }
  

  public checkInstructorStatus(): void {
    const courseId = this.route.snapshot.params['id'];
  
    this.userService.getUserInfo().subscribe(
      (user) => {
        const userId = user.id;
        console.log("User ID:", userId);
  
        this.courseService.isUserInstructor(userId, courseId).subscribe(
          (isInstructor: boolean) => {
            if (isInstructor) {
              console.log("User is instructor");
              this.isInstructor = true;
            } else {
              const isAdmin = this.loginService.isAdmin(); // asumido como síncrono
              if (isAdmin) {
                console.log("User is admin");
              } else {
                console.log("User is neither instructor nor admin");
              }
              this.isInstructor = isAdmin;
            }
          },
          (error) => {
            console.error("Error checking instructor status:", error);
            this.isInstructor = false;
          }
        );
      },
      (error) => {
        console.error("Error getting user info:", error);
        this.isInstructor = false;
      }
    );
  }

  public addComment(): void {
    const courseId = this.route.snapshot.params['id'];
    this.userService.getUserInfo().subscribe(
      (user) => {
        this.reviewService.addComment(this.comentarioTexto, user.id, courseId, null).subscribe(
          (comment) => {
            console.log("Comentario añadido con exito");
            this.comentarioTexto = ""
            this.loadReviews(courseId);
          },
          (error) => {console.log("Error al añadir el comentario", error)}
        );
      },
      (error) => {console.log("Error al obtener el usuario al añadir un comentario", error)}
    );
    //this.userService.getUserInfo().subscribe(
      //const courseId = this.route.snapshot.params['id'];
    //);
  }

  public addResponse(reviewId: number): void {
    const courseId = this.route.snapshot.params['id'];
    this.userService.getUserInfo().subscribe(
      (user) => {
        this.reviewService.addComment(this.comentarios[reviewId], user.id, courseId, reviewId).subscribe(
          (comment) => {
            console.log("Comentario añadido con exito");
            this.comentarios[reviewId] = ""
            this.loadReviews(courseId);
          },
          (error) => {console.log("Error al añadir el comentario", error)}
        );
      },
      (error) => {console.log("Error al obtener el usuario al añadir un comentario", error)}
    );
  }

  public markPending(reviewId: number): void {
    const courseId = this.route.snapshot.params['id'];
    this.reviewService.markPending(reviewId).subscribe(
      (response) => {
        console.log("Comentario marcado como pendiente con exito");
        this.loadReviews(courseId);
      },
      (error) => {console.log("Error al marcar el comentario como pendiente", error)}
    );
  }

  public goToMaterial() {
    this.active = 2;
  }
  public goToReviews() {
    this.active = 1;
  }

  loadPdf(courseId: number) {
      const pdfSubscription = this.courseService.getPdf(courseId)
        .subscribe(
          (data) => {
            const blob = new Blob([data], { type: 'application/pdf' });
            this.pdfSrc = { blob: blob };
          },
          (error) => {
            console.error('Error al obtener el PDF:', error);
          }
        );
  }

  pdfLoaded(pdf: PDFDocumentProxy) {
    this.totalPages = pdf.numPages;
  }

  zoomIn() {
    this.zoom += 0.25;
  }

  zoomOut() {
    if (this.zoom > 0.5) {
      this.zoom -= 0.25;
    }
  }

  downloadPdf() {
    // Si el pdfSrc es un blob
    if (this.pdfSrc && this.pdfSrc.blob) {
      const url = window.URL.createObjectURL(this.pdfSrc.blob);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.href = url;
      a.download = 'documento.pdf';
      a.click();
      window.URL.revokeObjectURL(url);
      a.remove();
    }
  }
}