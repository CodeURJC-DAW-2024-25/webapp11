import { Component, OnInit } from '@angular/core';
import Chart from 'chart.js/auto';
import { CourseService } from '../services/courses.service';

@Component({
  templateUrl: './chart.component.html',
  styleUrls: ["../app.component.css"],
})
export class ChartComponent implements OnInit {
  categories: { category: string, count: number }[] = [];
  courseCounts: { category: string, count: number }[] = [];  // Nuevo array para los cursos por categoría
  chart: Chart | undefined;
  chartCourses: Chart | undefined;  // Nuevo gráfico

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    // Obtener los datos de inscripciones
    this.courseService.getCategoryEnrollments().subscribe(data => {
      this.categories = data.map(d => ({
        category: d[0],
        count: d[1]
      }));
      this.createEnrollmentChart();
    });

    // Obtener los datos de cursos por categoría
    this.courseService.getCategoryCourses().subscribe(data => {
        const sortedData = data.sort((a, b) => b[1] - a[1]);

        // Tomar solo las 5 categorías con más cursos
        this.categories = sortedData.slice(0, 5).map(d => ({
          category: d[0],
          count: d[1]
        }));

        // Crear el gráfico con los datos obtenidos
        this.createCourseChart();
    });
  }

  // Crear gráfico de inscripciones
  createEnrollmentChart() {
    const labels = this.categories.map(c => c.category);
    const data = this.categories.map(c => c.count);

    this.chart = new Chart('categoryChart', {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Número de inscripciones',
          data: data,
          backgroundColor: 'rgba(153, 102, 255, 0.6)'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: true
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    });
  }

  // Crear gráfico de cursos por categoría
  createCourseChart() {
    const labels = this.categories.map(c => c.category);
    const data = this.categories.map(c => c.count);

    this.chart = new Chart('categoryCoursesChart', {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Número de cursos',
          data: data,
          backgroundColor: 'rgba(75, 192, 192, 0.6)'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: true
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    });
  }
}
