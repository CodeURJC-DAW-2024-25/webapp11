import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';
import { ReviewDto } from '../dtos/review.dto';
import { ReviewService } from '../services/reviews.service';
import { EnrollmentService } from '../services/enrollment.service';

@Component({
  selector: "app-course-detail",
  templateUrl: "./x.html",
})
export class CourseDetailComponent {

  public course!: CourseDto;
  reviews: ReviewDto[] = [];
  isEnrolled = false;
  isTeacher = false;
  isLogged = false;
  active = 1;
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private loginService: LoginService,
    private reviewService: ReviewService,
    private enrollmentService: EnrollmentService
  ) {
    const id = route.snapshot.params['id'];
    this.courseService.getCourseById(id).subscribe(
      (course) => (this.course = course),
      (error) => console.error(error)
    );

    this.reviewService.getReviewsByCourse(id).subscribe(
      (reviews) => (this.reviews = reviews),
      (error) => console.error(error)
    );

    this.isLogged = this.loginService.isLogged();
    const userId = this.loginService.currentUser()?.id;
    if (userId !== undefined) {
      this.enrollmentService.isUserEnrolled(userId, id).subscribe(
        (isEnrolled) => (this.isEnrolled = isEnrolled),
        (error) => {
          console.error(error);
          this.isEnrolled = false;
        }
      );
    } else {
      this.isEnrolled = false;
    }

    if (userId !== undefined) {
      this.courseService.isUserInstructor(userId, id).subscribe(
        (isTeacher) => (this.isTeacher = isTeacher),
        (error) => {
          console.error(error);
          this.isTeacher = false;
        }
      );
    } else {
      this.isTeacher = false;
    }

    if (this.isTeacher == false) {
      this.isTeacher = this.loginService.isAdmin() ?? false;
    }

  }
}
