import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-edit-course',
  templateUrl: './edit-course.component.html',
})
export class EditCourseComponent{
  public course!: CourseDto;
  topics = [
    'Desarrollo web',
    'Desarrollo móvil',
    'Desarrollo de videojuegos',
    'Emprendimiento',
    'Finanzas',
    'Marketing digital',
    'Liderazgo',
    'Comunicación'
  ];
  imageFileName: string | null = null;
  pdfFileName: string | null = null;

  image: File | null = null;
  pdf: File | null = null;

    @ViewChild('modalContent') modalContent!: TemplateRef<any>;  // Usar ng-template
    modalTitle = '';
    modalMessage = '';
    modalType: 'success' | 'error' = 'success';

  constructor(private courseService: CourseService, private router: Router, private route: ActivatedRoute, private modalService: NgbModal) {
    const courseId = this.route.snapshot.params['id'];
    this.courseService.getCourseById(courseId).subscribe(
      (course) => {
        this.course = course;
      },
      (error) => {
        console.error('Error al cargar el curso:', error);
      }
    );
  }

    onImageSelected(event: Event): void {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files[0]) {
        this.imageFileName = input.files[0].name;
        this.image = input.files[0];
      }
    }
  
    onPdfSelected(event: Event): void {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files[0]) {
        this.pdfFileName = input.files[0].name;
        this.pdf = input.files[0];
      }
    }

  onSubmit(): void {
    if (this.image) {
        this.courseService.updateCourseImage(this.course.id, this.image).subscribe({
          next: () => {
            console.log('Imagen actualizada con éxito');
            if (this.pdf) {
              this.courseService.updateCourseNotes(this.course.id, this.pdf).subscribe({
                next: () => {
                  console.log('Notas actualizadas con éxito');
                  this.courseService.updateCourse(this.course).subscribe(() => {
                    this.showModal('Curso actualizado', 'El curso y sus archivos se han actualizado exitosamente.', 'success');
                  });
                }
              });
            } else {
              this.courseService.updateCourse(this.course).subscribe(() => {
                this.showModal('Curso actualizado', 'El curso se ha actualizado exitosamente.', 'success');
              });
            }
          }
        });
    } else if (this.pdf) {
        this.courseService.updateCourseNotes(this.course.id, this.pdf).subscribe({
          next: () => {
            console.log('Notas actualizadas con éxito');
            this.courseService.updateCourse(this.course).subscribe(() => {
              this.showModal('Curso actualizado', 'El curso y sus archivos se han actualizado exitosamente.', 'success');
            });
          }
        });
    }
    else {
        this.courseService.updateCourse(this.course).subscribe(() => {
          this.showModal('Curso actualizado', 'El curso se ha actualizado exitosamente.', 'success');
        });
    }
    
    
  }

  onCancel(): void {
    this.router.navigate(['/courses']);
  }

  showModal(title: string, message: string, type: 'success' | 'error' = 'success') {
    this.modalTitle = title;
    this.modalMessage = message;
    this.modalType = type;
    console.log(this.modalContent)
    const modalRef = this.modalService.open(this.modalContent);  // Abrir modal

    // Si es un mensaje de éxito, redirigimos después de 5 segundos
    if (type === 'success') {
    setTimeout(() => {
        modalRef.close();  // Cerrar modal después de 5 segundos
        this.router.navigate(['/users/me']);  // Redirigir a la ruta /courses
    }, 5000);  // 5000 ms = 5 segundos
    }
}
}