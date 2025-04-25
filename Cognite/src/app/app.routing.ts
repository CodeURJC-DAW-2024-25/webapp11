import { Routes, RouterModule } from "@angular/router";

import { CourseDetailComponent  } from "./components/course-detail.component";
import { CourseListComponent } from "./components/course-list.component";
import { ProfileComponent } from "./components/profile.component";
import { EditCourseComponent } from "./components/edit-course.component";
import { CourseBrowseComponent } from "./components/course-browse.component";
import { CourseSearchComponent } from "./components/course-search.component";
import { UserFormComponent } from "./components/user-form.component";
const appRoutes: Routes = [
  { path: "courses", component: CourseListComponent },
  { path: "courses/topic/:category", component: CourseBrowseComponent },
  { path: "courses/:id", component: CourseDetailComponent },
  { path: "join", component: UserFormComponent },
  { path: "", redirectTo: "courses", pathMatch: "full" },
  { path: "users/me", component: ProfileComponent },
  { path: 'editCourse/:id', component: EditCourseComponent },
  { path: 'search/:query', component: CourseSearchComponent }

];

export const routing = RouterModule.forRoot(appRoutes);
