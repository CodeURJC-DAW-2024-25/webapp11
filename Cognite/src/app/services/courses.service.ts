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
    private apiUrl = 'https://localhost:8443/api/v1/courses';
  
    constructor(private http: HttpClient) {}
  
    getCourseById(id: number): Observable<CourseDto> {
      return this.http.get<CourseDto>(`${this.apiUrl}/${id}`);
    }
  }