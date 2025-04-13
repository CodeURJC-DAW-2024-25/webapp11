import { CourseDto } from "./course.dto";
import { UserDto } from "./user.dto";

export interface EnrollmentDto {
    id?: number;
    user: UserDto;
    course: CourseDto;
    rating: number;
    date: Date;
}

