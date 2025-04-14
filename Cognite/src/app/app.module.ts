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
@NgModule({
  declarations: [
    AppComponent,
    CourseDetailComponent,
    HeaderComponent,
    CourseListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    routing,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
