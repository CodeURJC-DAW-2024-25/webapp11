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
@Component({
  selector: "app-course-detail",
  templateUrl: "./x.html",
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
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private loginService: LoginService,
    private reviewService: ReviewService,
    private enrollmentService: EnrollmentService,
    private http: HttpClient
  ) {
    const courseId = this.route.snapshot.params['id'];
    this.loadCourseDetails(courseId);
    this.loadReviews(courseId);
    this.loadPdf(courseId);
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


  public checkEnrollmentStatus() {
    const userId = this.loginService.currentUser()?.id;
    const courseId = this.route.snapshot.params['id'];
    if (!userId) {
      return false;
    } else {
      return this.enrollmentService.isUserEnrolled(userId, courseId);
    }
  }

  public checkInstructorStatus() {
    const userId = this.loginService.currentUser()?.id;
    console.log("User ID: " + userId);
    const courseId = this.route.snapshot.params['id'];
    if (!userId) {
      console.log("User not logged in");
      return false;
    } else {
      const isInstructor = this.courseService.isUserInstructor(userId, courseId);
      if (isInstructor) {
        console.log("User is instructor");
        return true;
      } else {
        return this.loginService.isAdmin();
      }
    }
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