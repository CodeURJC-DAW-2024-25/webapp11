import { CourseDto } from "./course.dto";
import { UserDto } from "./user.dto";

export interface ReviewDto {
    id?: number;
    text: string;
    pending: boolean;
    user: UserDto;
    course: CourseDto;
    sons?: ReviewDto[];
    parent?: ReviewDto;
}
