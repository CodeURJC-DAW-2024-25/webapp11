import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { ReviewDto } from "../dtos/review.dto";


const BASE_URL = "/api/v1/reviews/";

@Injectable({ providedIn: 'root' })
export class ReviewService {

  constructor(private http: HttpClient) {}

     //Elimina una reseña por ID.
   deleteReview(id: number | string): Observable<void> {
     return this.http.delete<void>(`${BASE_URL}/${id}`);
   }

   // ignora reseña
   ignoreReview(id: number | string): Observable<void> {
     return this.http.post<void>(`${BASE_URL}/${id}/ignore`, {});
   }

   //reseñas pendientes
   getPendingReviews(): Observable<ReviewDto[]> {
     return this.http.get<ReviewDto[]>(`${BASE_URL}/pending`);
   }

  getReviewsByCourse(courseId: number | string): Observable<ReviewDto[]> {
    return this.http.get<ReviewDto[]>(BASE_URL + `course/`+courseId)
    .pipe(
      catchError((error) => this.handleError(error))
    ) as Observable<ReviewDto[]>;
  }

  editReview(review: ReviewDto): Observable<ReviewDto> {
    return this.http.put<ReviewDto>(BASE_URL + review.id, review)
    .pipe(
      catchError((error) => this.handleError(error))
    ) as Observable<ReviewDto>;
  }
  private handleError(error: any) {
    console.log("ERROR:");
    console.error(error);
    return throwError("Server error (" + error.status + "): " + error.text());
  }
}
