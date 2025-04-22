import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { CourseDto } from "../dtos/course.dto";


const BASE_URL = "/api/v1/courses/";

@Injectable({ providedIn: 'root'})
export class CourseService {
  
    constructor(private http: HttpClient) {}
  
    public getCourseById(id: number | string): Observable<CourseDto> {
      return this.http
      .get<CourseDto>(BASE_URL+ id )
      .pipe(
        catchError((error) => this.handleError(error))
      ) as Observable<CourseDto>;
    }

    getRecommendedCourses(): Observable<CourseDto[]> {
      return this.http.get<any[]>(`${BASE_URL}recommended`);
    }

    getTaughtCourses(id: number,page: number, size: number): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}taught/${id}/?page=${page}&size=${size}`);
    }
  
    getCourses(page: number, size: number): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}?page=${page}&size=${size}`);
    }

    public isUserInstructor (userId: number, courseId: number): Observable<boolean> {
      return this.http.get<boolean>(`${BASE_URL}${userId}/${courseId}`);
    }

    getPdf(courseId: number): Observable<Blob> {
      return this.http.get(`${BASE_URL}${courseId}/notes`, { responseType: 'blob' });
    }


    private handleError(error: any) {
      console.log("ERROR:");
      console.error(error);
      return throwError("Server error (" + error.status + "): " + error.text());
    }

  }