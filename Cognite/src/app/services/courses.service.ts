import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { CourseDto } from "../dtos/course.dto";


const BASE_URL = "/api/v1/courses";

@Injectable({
    providedIn: 'root'
  })
  export class CourseService {
  
    constructor(private http: HttpClient) {}
  
    getCourseById(id: number): Observable<CourseDto> {
      return this.http.get<CourseDto>(`${BASE_URL}/${id}`);
    }

    getRecommendedCourses(): Observable<CourseDto[]> {
      return this.http.get<any[]>(`${BASE_URL}/recommended`);
    }
  
    getCourses(page: number, size: number): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}/?page=${page}&size=${size}`);
    }

    public isUserInstructor (userId: number, courseId: number): Observable<boolean> {
      return this.http.get<boolean>(`${BASE_URL}/${userId}/${courseId}`);
    }

  }