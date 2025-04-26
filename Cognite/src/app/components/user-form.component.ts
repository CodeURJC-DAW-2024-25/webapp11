import { Component, ViewChild, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { UserFormDto } from '../dtos/userForm.dto';
import { HttpErrorResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
declare const bootstrap: any;
@Component({
    selector: 'app-user-form',
    templateUrl: './user-form.component.html',
    styleUrls: ['../app.component.css']
})
export class UserFormComponent {
    @ViewChild('modalContent') modalContent!: TemplateRef<any>;  // Usar ng-template
    modalTitle = '';
    modalMessage = '';
    modalType: 'success' | 'error' = 'success';
    userForm: FormGroup;
    selectedFile: File | null = null;
    topics: string[] = [
    'Desarrollo web', 'Desarrollo móvil', 'Desarrollo de videojuegos',
    'Emprendimiento', 'Finanzas', 'Marketing digital',
    'Liderazgo', 'Comunicación'
    ];

    constructor(private fb: FormBuilder, private userService: UserService, private modalService: NgbModal, private router: Router) {
        this.userForm = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        topic: ['', Validators.required],
        password: ['', Validators.required],
        repeatPassword: ['', Validators.required]
        });
    }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

onSubmit() {
  if (this.userForm.invalid || this.userForm.value.password !== this.userForm.value.repeatPassword) {
    this.showModal('Error', 'Formulario inválido o contraseñas no coinciden', 'error');
    return;
  }

  const user: UserFormDto = {
    firstName: this.userForm.value.firstName,
    lastName: this.userForm.value.lastName,
    email: this.userForm.value.email,
    topic: this.userForm.value.topic,
    password: this.userForm.value.password,
    roles: ['USER']
  };

  this.userService.createUser(user).subscribe({
    next: (createdUser) => {
      const userId = createdUser.id;

      if (this.selectedFile) {
        this.userService.createUserImage(userId, this.selectedFile).subscribe({
          next: () => {
            this.showModal('Éxito', 'Usuario creado exitosamente', 'success');
          },
          error: (err) => {
            this.showModal('Error', 'Error subiendo imagen: ' + err.message, 'error');
          }
        });
      } else {
        this.showModal('Éxito', 'Usuario creado (sin imagen)', 'success');
      }
    },
    error: (err: HttpErrorResponse) => {
      if (err.status === 400) {
        this.showModal('Error', 'El usuario ya está registrado', 'error');
      } else {
        this.showModal('Error', 'Error creando usuario: ' + err.message, 'error');
      }
    }
  });
}



  showModal(title: string, message: string, type: 'success' | 'error' = 'success') {
    this.modalTitle = title;
    this.modalMessage = message;
    this.modalType = type;
    const modalRef = this.modalService.open(this.modalContent);  // Abrir modal

    // Si es un mensaje de éxito, redirigimos después de 5 segundos
    if (type === 'success') {
      setTimeout(() => {
        modalRef.close();  // Cerrar modal después de 5 segundos
        this.router.navigate(['/courses']);  // Redirigir a la ruta /courses
      }, 5000);  // 5000 ms = 5 segundos
    }
  }
}