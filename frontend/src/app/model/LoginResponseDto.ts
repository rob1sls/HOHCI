import { Country } from "./country";
import { User } from "./user";

export class LoginResponseDto {
    id: number;
    user: User;
    group: string;
}