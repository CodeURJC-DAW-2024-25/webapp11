import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../services/courses.service';
import { CourseDto } from '../dtos/course.dto';

@Component({
  selector: 'app-edit-course',
  templateUrl: './edit-course.component.html',
  //styleUrls: ['./edit-course.component.css']
})
export class EditCourseComponent implements OnInit {
  public course!: CourseDto;
  topics = [
    'Desarrollo web',
    'Desarrollo móvil',
    'Desarrollo de videojuegos',
    'Emprendimiento',
    'Finanzas',
    'Marketing digital',
    'Liderazgo',
    'Comunicacion'
  ];
  imageFileName: string | null = null;
  pdfFileName: string | null = null;

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

  ngOnInit(): void {
    // Aquí puedes cargar los datos del curso si es necesario
    this.loadCourse();
  }

  loadCourse(): void {
    // Simulación de carga de datos del curso
    /*this.course = {
      id: 1,
      title: 'Curso de ejemplo',
      description: 'Descripción del curso de ejemplo',
      topic: 'desarrollo-web'
    };*/
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.imageFileName = input.files[0].name;
    }
  }

  onPdfSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.pdfFileName = input.files[0].name;
    }
  }

  onSubmit(): void {
    console.log('Datos del curso:', this.course);
    // Aquí puedes enviar los datos al backend
    this.courseService.updateCourse(this.course).subscribe(() => {
      alert('Curso actualizado con éxito');
      this.router.navigate(['/courses']);
    });
  }

  onCancel(): void {
    this.router.navigate(['/courses']);
  }
}