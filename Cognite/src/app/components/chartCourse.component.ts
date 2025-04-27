import { Component, OnInit, AfterViewInit, AfterViewChecked, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChartCourseService } from '../services/chartCourse.service';
import Chart from 'chart.js/auto'; // Importamos Chart.js

@Component({
  templateUrl: './chartCourse.component.html',
  styleUrls: ["../app.component.css"],
})
export class ChartCourseComponent implements OnInit, AfterViewChecked {
  @ViewChild('ratingsChart') ratingsChartRef!: ElementRef;

  courseId!: number;
  isLoading = true;
  ratings: { stars: number, count: number }[] = [];
  chart: Chart | undefined; // para guardar el gráfico

  constructor(
    private route: ActivatedRoute,
    private chartCourseService: ChartCourseService
  ) {}

  ngOnInit(): void {
    this.courseId = Number(this.route.snapshot.paramMap.get('id'));
    this.chartCourseService.getCourseRatings(this.courseId).subscribe(data => {
      this.ratings = data.map(d => ({
        stars: d[0],
        count: d[1]
      }));
      this.isLoading = false; // Los datos han llegado, ya podemos renderizar el gráfico
      this.createChart(); // Llamamos a crear el gráfico ahora que los datos están disponibles
    });
  }

  ngAfterViewChecked() {
    // Asegurémonos de que los datos están disponibles antes de crear el gráfico
    if (this.ratingsChartRef && this.ratingsChartRef.nativeElement && this.ratings.length > 0 && !this.chart) {
      this.createChart(); // Creamos el gráfico solo si los datos están cargados y el gráfico no se ha creado aún
    }
  }

  createChart() {
    if (!this.ratingsChartRef?.nativeElement) {
      console.error('Canvas no encontrado');
      return;
    }

    const labels = this.ratings.map(r => `${r.stars} estrellas`);
    const data = this.ratings.map(r => r.count);

    // Si ya existe un gráfico, lo destruye antes de crear uno nuevo
    if (this.chart) {
      this.chart.destroy();
    }

    // Crear el gráfico
    this.chart = new Chart(this.ratingsChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Número de votos',
          data: data,
          backgroundColor: 'rgba(75, 192, 192, 0.7)'
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
