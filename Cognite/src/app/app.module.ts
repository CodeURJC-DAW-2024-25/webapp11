//import { ChartsComponent } from './components/chart.component';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { routing } from "./app.routing";
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { HttpClientModule } from '@angular/common/http';
import { CourseDetailComponent } from './components/course-detail.component';
import { HeaderComponent } from './components/header.component';
import { FormsModule } from '@angular/forms';
import { CourseListComponent } from './components/course-list.component';
import { ProfileComponent } from "./components/profile.component";
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { ReactiveFormsModule } from '@angular/forms';
import { EditCourseComponent } from './components/edit-course.component';
import { CourseBrowseComponent } from './components/course-browse.component';
import { CourseSearchComponent } from './components/course-search.component';
import { UserFormComponent } from './components/user-form.component';
import { CourseFormComponent } from './components/course-form.component';

@NgModule({
  declarations: [
    AppComponent,
    CourseDetailComponent,
    HeaderComponent,
    CourseListComponent,
    ProfileComponent,
    EditCourseComponent,
    CourseBrowseComponent,
    CourseSearchComponent,
    UserFormComponent,
    CourseFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    routing,
    NgbModule,
    PdfViewerModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
