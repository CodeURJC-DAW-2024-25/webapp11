import { Component, OnInit } from '@angular/core';
import { CourseService } from '../services/courses.service';
import { Router } from "@angular/router";
@Component({
  templateUrl: './course-list.component.html',
  styleUrl: '../app.component.css'
})
export class CourseListComponent implements OnInit {
  recomendedCourses: any[] = [];
  allCourses: any[] = [];
  loading = false;
  recomendLoading = false;
  hasMoreCourses = true;
  currentPage = 0;
  pageSize = 10;

  constructor(private courseService: CourseService,
              private router: Router
  ) {}

  public ngOnInit() {
    this.loadRecommendedCourses();
    this.loadCourses();
  }

  public loadRecommendedCourses(): void {
    this.recomendLoading = true;
    this.courseService.getRecommendedCourses().subscribe({
      next: (courses) => {
        this.recomendedCourses= courses;
        this.recomendLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar cursos recomendados:', err);
      }
      
    });
  }

  public loadCourses(): void {
    this.loading = true;
    this.courseService.getCourses(this.currentPage, this.pageSize).subscribe({
      next: (courses) => {
        if (courses.length === 0) {
          this.hasMoreCourses = false;
        } else {
          this.allCourses = [...this.allCourses, ...courses];
          this.currentPage++;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar cursos:', err);
        this.loading = false;
      }
    });
  }

  public loadMore(): void {
    this.loadCourses();
  }
}

