import { Routes, RouterModule } from "@angular/router";

import { CourseDetailComponent  } from "./components/course-detail.component";
import { CourseListComponent } from "./components/course-list.component";
import { ProfileComponent } from "./components/profile.component";

const appRoutes: Routes = [
  { path: "courses", component: CourseListComponent },
  //{ path: "books/new", component: BookFormComponent },
  { path: "courses/:id", component: CourseDetailComponent },
  //{ path: "books/edit/:id", component: BookFormComponent },
  { path: "", redirectTo: "courses", pathMatch: "full" },
  { path: "users/:id", component: ProfileComponent },
];

export const routing = RouterModule.forRoot(appRoutes);
