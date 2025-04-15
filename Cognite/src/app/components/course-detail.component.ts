import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';
import { ReviewDto } from '../dtos/review.dto';
import { ReviewService } from '../services/reviews.service';

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

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private loginService: LoginService,
    private reviewService: ReviewService
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
  }

}
