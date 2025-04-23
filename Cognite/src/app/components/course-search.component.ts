import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { Router } from "@angular/router";
import { Component, OnInit } from '@angular/core';

@Component({
  templateUrl: './course-browse.component.html',
  styleUrl: '../app.component.css'
})
export class CourseSearchComponent implements OnInit {
  query: string = '';
  allCourses: any[] = [];
  loading = false;
  hasMoreCourses = true;
  currentPage = 0;
  pageSize = 10;

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Escucha los cambios en el parámetro de la ruta
    this.route.paramMap.subscribe(params => {
      this.query = params.get('query') || '';
      this.resetCourseList();  // 👈 Limpiar y recargar cursos
      this.loadCourses();
    });
  }

  private resetCourseList(): void {
    this.allCourses = [];
    this.loading = false;
    this.hasMoreCourses = true;
    this.currentPage = 0;
  }

  public loadCourses(): void {
    this.loading = true;
    this.courseService.getCoursesByTitle(this.query, this.currentPage, this.pageSize).subscribe({
      next: (courses) => {
        if (courses.length === 0) {
          this.hasMoreCourses = false;
        } else {
          this.allCourses = [...this.allCourses, ...courses];
          this.currentPage++;
          if (courses.length < this.pageSize) {
            this.hasMoreCourses = false; // No hay más cursos para cargar
          }
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
