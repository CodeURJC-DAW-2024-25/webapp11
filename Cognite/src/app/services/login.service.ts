import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject } from "rxjs";
import { UserDto } from "../dtos/user.dto";

const BASE_URL = "/api/v1/auth";

@Injectable({ providedIn: "root" })
export class LoginService {
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedInSubject.asObservable();

  public user?: UserDto;

  constructor(private http: HttpClient) {
    this.reqIsLogged();
  }

  public reqIsLogged() {
    this.http.get("/api/v1/users/me", { withCredentials: true }).subscribe(
      (response) => {
        this.user = response as UserDto;
        this.loggedInSubject.next(true);
      },
      (error) => {
        if (error.status != 404) {
          console.error("Error when asking if logged: " + JSON.stringify(error));
        }
        this.loggedInSubject.next(false);
      }
    );
  }

  public logIn(user: string, pass: string) {
    return this.http.post(
      BASE_URL + "/login",
      { username: user, password: pass },
      { withCredentials: true }
    ).pipe(); // sigue siendo un observable, el componente debe suscribirse
  }

  public logOut() {
    return this.http
      .post(BASE_URL + "/logout", { withCredentials: true })
      .subscribe((_) => {
        console.log("LOGOUT: Successfully");
        this.loggedInSubject.next(false);
        this.user = undefined;
      });
  }

  public isAdmin() {
    return this.user?.roles.includes("ADMIN") ?? false;
  }

  public currentUser() {
    return this.user;
  }

  public isLogged() {
    return this.loggedInSubject.getValue();
  }
}
