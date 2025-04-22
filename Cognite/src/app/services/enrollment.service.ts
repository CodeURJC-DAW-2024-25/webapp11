import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
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
        return this.http.get<boolean>(`${BASE_URL}/${userId}/${courseId}`);
    }
  }