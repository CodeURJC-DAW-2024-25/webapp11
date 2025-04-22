import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChartCourseService } from '../services/chartCourse.service';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-chart-course',
  templateUrl: '../app.component.html'
})
export class ChartCourseComponent implements OnInit {
  courseId!: number;
  chartOptions!: ChartConfiguration<'bar'>;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private chartCourseService: ChartCourseService
  ) {}

  ngOnInit(): void {
    this.courseId = Number(this.route.snapshot.paramMap.get('id'));
    this.chartCourseService.getCourseRatings(this.courseId).subscribe(data => {
      this.chartOptions = {
        type: 'bar',
        data: {
          labels: data.labels,
          datasets: [{
            label: 'Valoraciones',
            data: data.values,
            backgroundColor: 'rgba(75,192,192,0.6)'
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { display: true },
            title: { display: true, text: 'Valoraciones del Curso' }
          }
        }
      };
      this.isLoading = false;
    });
  }
}
