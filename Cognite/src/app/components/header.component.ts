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

  @ViewChild('loginErrorModal', { static: true })
  public loginErrorModal: TemplateRef<void> | undefined;
  
  constructor(
    public loginService: LoginService, // Cambiado a público para acceder desde la plantilla
    private router: Router,
    private modalService: NgbModal
  ) {}

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
  searchCourse(event: Event): void {
    event.preventDefault();

    const query = this.findCourseText.trim();
    if (query) {
      this.router.navigate(['/search', query]);
      this.findCourseText = ''; // opcional: limpiar el campo después
    }
  }
}