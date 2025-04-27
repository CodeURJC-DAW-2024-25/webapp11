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

    updateCourse(course: any): Observable<any> {
      return this.http.put(`/api/v1/courses/${course.id}`, course);
    }

    updateCourseImage(courseId: any, imageFile: File): Observable<any> {
      const formData = new FormData();
      formData.append('imageFile', imageFile);
      return this.http.put(`${BASE_URL}${courseId}/image`, formData);
    }

    updateCourseNotes(courseId: any, notesFile: File): Observable<any> {
      const formData = new FormData();
      formData.append('noteFile', notesFile);
      return this.http.put(`${BASE_URL}${courseId}/notes`, formData);
    }

    getCoursesByTopic(topic: string, page: number, size: number): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}topic/${topic}?page=${page}&size=${size}`);
    }

    getCoursesByTitle(title: string, page: number, size: number): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}title/${title}?page=${page}&size=${size}`);
    }

    createCourse(course: CourseDto): Observable<CourseDto> {
      return this.http.post<CourseDto>(BASE_URL, course);
    }

    createCourseImage (courseId: number, imageFile: File): Observable<any> {
      const formData = new FormData();
      formData.append('imageFile', imageFile);
      return this.http.post(`${BASE_URL}${courseId}/image`, formData);
    }

    createCourseNotes (courseId: number, notesFile: File): Observable<any> {
      const formData = new FormData();
      formData.append('noteFile', notesFile);
      return this.http.post(`${BASE_URL}${courseId}/notes`, formData);
    }

    deleteCourse(id: number | undefined): Observable<void> {
      return this.http.delete<void>(`${BASE_URL}/${id}`, {
        withCredentials: true,
        responseType: 'text' as 'json'
      });
    }

    getCategoryEnrollments(): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}mostpopular`);
    }

    getCategoryCourses(): Observable<any[]> {
      return this.http.get<any[]>(`${BASE_URL}most-courses`);
    }
  }