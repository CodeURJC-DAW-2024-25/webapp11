import { Component, OnInit } from '@angular/core';
import { CourseService } from './services/courses.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  recomendedCourses: any[] = [];
  allCourses: any[] = [];
  loading = false;
  recomendLoading = false;
  hasMoreCourses = true;
  currentPage = 0;
  pageSize = 10;

  constructor(private courseService: CourseService) {}

  ngOnInit() {
    this.loadRecommendedCourses();
    this.loadCourses();
  }

  loadRecommendedCourses(): void {
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

  loadCourses(): void {
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

  loadMore(): void {
    this.loadCourses();
  }
}

