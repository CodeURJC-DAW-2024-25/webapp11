// components/course-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';

@Component({
  selector: 'app-course-detail',
  template: `
    <div *ngIf="course">
        <h2>{{ course.title }}</h2>
        <p><strong>Descripción:</strong> {{ course.description }}</p>
        <p><strong>Tema:</strong> {{ course.topic }}</p>
        <p><strong>Instructor:</strong> {{ course.instructor.firstName }} {{ course.instructor.lastName }}</p>
        <p><strong>Email del instructor:</strong> {{ course.instructor.email }}</p>
        <p><strong>Rating:</strong> {{ course.rating }}</p>
    </div>
    `
})
export class CourseDetailComponent implements OnInit {
  course?: CourseDto;

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.courseService.getCourseById(1).subscribe({
      next: (data) => this.course = data,
      error: (err) => console.error('Error al obtener curso:', err)
    });
  }
}
