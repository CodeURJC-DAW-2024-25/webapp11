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
import { AdminComponent } from './components/admin.component';

@NgModule({
  declarations: [
    AppComponent,
    CourseDetailComponent,
    HeaderComponent,
    CourseListComponent,
    ProfileComponent,
    AdminComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    routing,
    NgbModule,
    PdfViewerModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
