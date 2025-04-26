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

  getCourseRatings(courseId: number): Observable<CourseRatingChartData> {
    return this.http.get<CourseRatingChartData>(`/api/v1/enrollments/statistics/${courseId}`);
  }
}
