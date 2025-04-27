import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface CourseRatingChartData {
  labels: string[];
  values: number[];
}

@Injectable({
  providedIn: 'root'
})
export class ChartCourseService {
  constructor(private http: HttpClient) {}

  getCourseRatings(courseId: number): Observable<number[][]> {
    return this.http.get<number[][]>(`/api/v1/enrollments/statistics/${courseId}`);
  }
}
