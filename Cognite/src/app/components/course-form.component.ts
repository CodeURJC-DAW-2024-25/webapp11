import { Component, ViewChild, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';
import { UserService } from '../services/user.service';
declare const bootstrap: any;
@Component({
    templateUrl: './course-form.component.html',
    styleUrls: ['../app.component.css']
})
export class CourseFormComponent {
    @ViewChild('modalContent') modalContent!: TemplateRef<any>;  // Usar ng-template
    modalTitle = '';
    modalMessage = '';
    modalType: 'success' | 'error' = 'success';
    courseForm: FormGroup;
    imageFile: File | null = null;
    notesFile: File | null = null;


    constructor(
        private fb: FormBuilder, 
        private modalService: NgbModal, 
        private router: Router, 
        private courseService: CourseService, 
        private userService: UserService
    ) {
        this.courseForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', [Validators.required, Validators.maxLength(200)]],
            topic: ['', Validators.required],
        });
    }

    onFileSelected(event: Event, type: 'image' | 'notes') {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            if (type === 'image') {
                this.imageFile = file;
            } else if (type === 'notes') {
                this.notesFile = file;
            }
        }
    }

    onSubmit() {
        if (this.courseForm.valid) {
          this.userService.getUserInfo().subscribe({
            next: (user) => {
              const course: CourseDto = {
                title: this.courseForm.value.title,
                description: this.courseForm.value.description,
                topic: this.courseForm.value.topic,
                instructor: user,
                rating: 0
              };
      
              this.courseService.createCourse(course).subscribe({
                next: (createdCourse) => {
                  // Curso creado exitosamente
                  this.showModal('Curso creado', 'El curso ha sido creado exitosamente.', 'success');
      
                  // Subir imagen si existe
                  if (this.imageFile) {
                    this.courseService.createCourseImage(createdCourse.id!, this.imageFile).subscribe({
                      next: () => {
                        console.log('Imagen subida correctamente');
                      },
                      error: (err) => {
                        console.error('Error subiendo imagen', err);
                        this.showModal('Error', 'Hubo un error al subir la imagen.', 'error');
                      }
                    });
                  }
      
                  // Subir notas si existe
                  if (this.notesFile) {
                    this.courseService.createCourseNotes(createdCourse.id!, this.notesFile).subscribe({
                      next: () => {
                        console.log('Notas subidas correctamente');
                      },
                      error: (err) => {
                        console.error('Error subiendo notas', err);
                        this.showModal('Error', 'Hubo un error al subir las notas.', 'error');
                      }
                    });
                  }
                },
                error: (err) => {
                  console.error('Error creando curso', err);
                  this.showModal('Error', 'Hubo un error al crear el curso.', 'error');
                }
              });
            },
            error: (err) => {
              console.error('Error obteniendo usuario', err);
              this.showModal('Error', 'No se pudo obtener la información del usuario.', 'error');
            }
          });
        } else {
          this.showModal('Formulario inválido', 'Por favor completa todos los campos requeridos.', 'error');
        }
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
        this.router.navigate(['/users/me']);  // Redirigir a la ruta /courses
      }, 5000);  // 5000 ms = 5 segundos
    }
  }

  cancel() {
    this.router.navigate(['/users/me']);
  }
}