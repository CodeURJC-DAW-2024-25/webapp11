import { UserDto } from "./user.dto";

export interface CourseDto {
    id?: number;
    title: string;
    description: string;
    topic: string;
    instructor: UserDto;
    rating: number;
}