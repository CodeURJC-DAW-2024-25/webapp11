import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { EnrollmentDto } from "../dtos/enrollment.dto";


const BASE_URL = "/api/v1/enrollments";

@Injectable({
    providedIn: 'root'
  })
  export class EnrollmentService {
  
    constructor(private http: HttpClient) {}
  
    public getEnrollmentsByUserId(userId: number): Observable<EnrollmentDto[]> {
        return this.http.get<EnrollmentDto[]>(`${BASE_URL}/${userId}`);
    }

    public isUserEnrolled (userId: number, courseId: number) {
        return this.http.get<boolean>(`${BASE_URL}/${userId}/${courseId}`);
    }
  }