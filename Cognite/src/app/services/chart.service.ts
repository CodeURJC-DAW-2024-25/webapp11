import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Aquí no necesitamos un archivo de entorno, usamos la URL base para las estadísticas.
const BASE_URL = '/api/v1/statistics';  // Esta URL será redirigida por el proxy

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  constructor(private http: HttpClient) {}

  // Método para obtener las estadísticas
  getStats(): Observable<any> {
    return this.http.get<any>(BASE_URL);  // Usamos la URL base directamente
  }
}


