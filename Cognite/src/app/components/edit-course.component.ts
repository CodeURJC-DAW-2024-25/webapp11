import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';

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

  constructor(private courseService: CourseService, private router: Router, private route: ActivatedRoute) {
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
        this.courseService.updateCourseImage(this.course.id, this.image).subscribe(
          () => {
            console.log('Imagen actualizada con éxito');
          }
        );
    }
    if (this.pdf) {
        this.courseService.updateCourseNotes(this.course.id, this.pdf).subscribe(
          () => {
            console.log('Notas actualizadas con éxito');
          }
        );
    }
    this.courseService.updateCourse(this.course).subscribe(() => {
      alert('Curso actualizado con éxito');
      this.router.navigate(['/courses']);
    });
  }

  onCancel(): void {
    this.router.navigate(['/courses']);
  }
}