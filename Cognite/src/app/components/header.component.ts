import { Component, ViewChild, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { UserDto } from '../dtos/user.dto';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['../app.component.css']
})
export class HeaderComponent {
  findCourseText = '';
  showLoginForm = false;
  loginEmail = '';
  loginPassword = '';
  user?: UserDto;

  @ViewChild('loginErrorModal', { static: true })
  public loginErrorModal: TemplateRef<void> | undefined;
  
  constructor(
    public loginService: LoginService, // Cambiado a público para acceder desde la plantilla
    private router: Router,
    private modalService: NgbModal
  ) {}

  public searchCourse(event: Event) {
    event.preventDefault();
    if (this.findCourseText.trim()) {
      this.router.navigate(['/search'], { 
        queryParams: { query: this.findCourseText }
      });
    }
  }

  public logOut() {
    this.loginService.logOut();
  }

  public submitLogin() {
    this.logIn(this.loginEmail, this.loginPassword);
  }

  public logIn(email: string, password: string) {
    this.loginService.logIn(email, password).subscribe(
      (_) => {
        this.loginService.reqIsLogged();
        this.toggleLoginForm(); // Cerrar el formulario de inicio de sesión
      },
      (_) => {
        this.modalService.open(this.loginErrorModal, { centered: true });
        this.loginEmail = '';
        this.loginPassword = '';
      }
    );
  }

  public toggleLoginForm() {
    this.showLoginForm = !this.showLoginForm;
    // Resetear campos si se cierra el formulario
    if (!this.showLoginForm) {
      this.loginEmail = '';
      this.loginPassword = '';
    }
  }
}