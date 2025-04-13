import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['../app.component.css']
})
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  findCourseText = '';
  
  constructor(
    private loginService: LoginService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Verificar si el usuario está autenticado
    this.isLoggedIn = this.loginService.isLogged();
    this.loginService.reqIsLogged(); // Actualizar el estado de autenticación
  }

  searchCourse(event: Event): void {
    event.preventDefault();
    if (this.findCourseText.trim()) {
      this.router.navigate(['/search'], { 
        queryParams: { query: this.findCourseText }
      });
    }
  }

  logout(): void {
    this.loginService.logOut();
    this.isLoggedIn = false;
    this.router.navigate(['/']);
  }
}