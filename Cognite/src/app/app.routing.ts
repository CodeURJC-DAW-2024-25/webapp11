import { Routes, RouterModule } from "@angular/router";

import { CourseDetailComponent  } from "./components/course-detail.component";
import { CourseListComponent } from "./components/course-list.component";
import { ProfileComponent } from "./components/profile.component";
import { EditCourseComponent } from "./components/edit-course.component";
import { CourseBrowseComponent } from "./components/course-browse.component";
import { CourseSearchComponent } from "./components/course-search.component";
import { UserFormComponent } from "./components/user-form.component";
import { CourseFormComponent } from "./components/course-form.component";
import { ChartCourseComponent } from "./components/chartCourse.component";
import { ChartComponent } from "./components/chart.component";
const appRoutes: Routes = [
  { path: "courses", component: CourseListComponent },
  { path: "courses/new", component: CourseFormComponent },
  { path: "courses/topic/:category", component: CourseBrowseComponent },
  { path: "courses/:id", component: CourseDetailComponent },
  { path: "join", component: UserFormComponent },
  { path: "charts", component: ChartComponent },
  { path: "", redirectTo: "courses", pathMatch: "full" },
  { path: "users/me", component: ProfileComponent },
  { path: 'editCourse/:id', component: EditCourseComponent },
  { path: 'search/:query', component: CourseSearchComponent },
  { path: "chartsCourse/:id", component: ChartCourseComponent}

];

export const routing = RouterModule.forRoot(appRoutes);
