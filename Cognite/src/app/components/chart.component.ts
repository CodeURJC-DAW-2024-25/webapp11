import { Component, OnInit } from '@angular/core';
import { StatisticsService } from '../services/chart.service';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';  // Importamos el servicio correcto
import { UserDto } from '../dtos/user.dto';  // Usamos el DTO correcto

import {
  ApexAxisChartSeries,
  ApexChart,
  ApexDataLabels,
  ApexPlotOptions,
  ApexYAxis,
  ApexTitleSubtitle,
  ApexXAxis,
  ApexFill
} from 'ng-apexcharts';

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  dataLabels: ApexDataLabels;
  plotOptions: ApexPlotOptions;
  yaxis: ApexYAxis;
  xaxis: ApexXAxis;
  fill: ApexFill;
  title: ApexTitleSubtitle;
};

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['../../assets/css/bootstrap.css', '../../assets/css/progress.css']
})
export class StatisticsComponent implements OnInit {
  chartOptions: ChartOptions | undefined;
  admin: boolean | undefined;
  user: UserDto | undefined;  // Usamos UserDto en vez de Person
  roles: string[] | undefined;  // Definimos roles como un array de strings

  constructor(
    private statisticsService: StatisticsService,
    public userService: UserService,  // Usamos UserService
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadCharts();  // Llamada para cargar los gráficos

    // Obtenemos la información del usuario actual
    this.userService.getUserInfo().subscribe(
      response => {
        this.user = response;  // Asignamos los datos del usuario
        this.roles = this.user.roles;  // Obtenemos los roles
        this.admin = this.roles.includes('ADMIN');  // Verificamos si el usuario es ADMIN
      },
      error => {
        this.router.navigate(['../login']);
        this.user = { id: 0, firstName: '', lastName: '', email: '', topic: '', roles: [] };  // Establecemos valores por defecto si hay error
      }
    );
  }

  loadCharts() {
    // Llamada al servicio para obtener las estadísticas desde el backend
    this.statisticsService.getStats().subscribe(
      (response: any) => {
        // Mapeamos los datos que obtenemos desde el backend
        const coursesCategoriesData = Object.values(response.mostCoursesCategories).map(value => parseFloat((value as string | number).toString()));
        const inscribedCategoriesData = Object.values(response.mostInscribedCategories as Record<string, number>).map(value => parseFloat(value.toString()));

        const coursesCategoriesLabels = Object.keys(response.mostCoursesCategories);
        const inscribedCategoriesLabels = Object.keys(response.mostInscribedCategories);

        // Configuración de los gráficos
        this.chartOptions = {
          series: [
            {
              name: "Cursos",
              data: coursesCategoriesData
            },
            {
              name: "Inscripciones",
              data: inscribedCategoriesData
            }
          ],
          chart: {
            height: 600,
            width: 800,
            type: "bar",
            background: '#ffffff'
          },
          plotOptions: {
            bar: {
              dataLabels: { position: "top" }
            }
          },
          dataLabels: {
            enabled: true,
            formatter: (val) => val + "",
            offsetY: -20,
            style: { fontSize: "12px", colors: ["#304758"] }
          },
          xaxis: {
            categories: coursesCategoriesLabels,  // Usamos las mismas categorías para ambos gráficos
            position: "bottom",
            labels: { offsetY: 0 },
            axisBorder: { show: false },
            axisTicks: { show: false },
            tooltip: { enabled: true, offsetY: -35 }
          },
          fill: { colors: ["#f1db25", "#32a852"] },
          yaxis: {
            axisBorder: { show: false },
            axisTicks: { show: false },
            labels: { show: false }
          },
          title: {
            text: "Categorías más populares (Cursos vs Inscripciones)",
            offsetY: 0,
            align: "center",
            style: { color: "black" }
          }
        };
      },
        (      error: any) => {
        console.error('Error obteniendo estadísticas:', error);
      }
    );
  }
}
