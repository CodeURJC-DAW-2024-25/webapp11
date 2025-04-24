import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { EnrollmentDto } from "../dtos/enrollment.dto";
import { CourseDto } from "../dtos/course.dto";


const BASE_URL = "/api/v1/enrollments";

@Injectable({
    providedIn: 'root'
  })
  export class EnrollmentService {
  
    constructor(private http: HttpClient) {}
  
    public getEnrolledCourses(userId: number, page:number, size: number): Observable<CourseDto[]> {
        return this.http.get<CourseDto[]>(`${BASE_URL}/${userId}/?page=${page}&size=${size}`);
    }

    public isUserEnrolled (userId: number, courseId: number) {
        return this.http.get<number>(`${BASE_URL}/${userId}/${courseId}`);
    }

    public enrollCourse(userId: number, courseId: number): Observable<EnrollmentDto> {
      const url = `${BASE_URL}/${courseId}`;
      const params = new HttpParams().set('userId', userId.toString());
      return this.http.post<EnrollmentDto>(url, null, { params });
    }
    
    public rateCourse(enrollmentId: Number, rating: number): Observable<EnrollmentDto> {
      const url = `${BASE_URL}/${enrollmentId}`;
      const params = new HttpParams().set('rating', rating.toString());
      return this.http.put<EnrollmentDto>(url, null, { params });
    }

  }