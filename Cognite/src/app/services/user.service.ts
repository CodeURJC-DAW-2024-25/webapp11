import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from '../dtos/user.dto';
import { UserFormDto } from '../dtos/userForm.dto';

const BASE_URL = "/api/v1/users";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {}

  getUserById(id: number): Observable<UserDto> {
      return this.http.get<UserDto>(`${BASE_URL}/${id}`);
  }

  /**
   * Obtiene la información del usuario actual.
   * @returns Un observable con los datos del usuario.
   */
  getUserInfo(): Observable<UserDto> {
    return this.http.get<UserDto>(`${BASE_URL}/me`);
  }
  /**
 * Obtiene todos los usuarios (solo para administradores).
 */
  getAllUsers(): Observable<UserDto[]> {
  return this.http.get<UserDto[]>(`${BASE_URL}`);
  }

  updateUser(user: Partial<UserDto>): Observable<UserDto> {
    return this.http.put<UserDto>(`${BASE_URL}/me`, user);
  }

  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${BASE_URL}/me/change-password`, {
      currentPassword,
      newPassword
    });
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`, {
      withCredentials: true,
      responseType: 'text' as 'json'
    });
  }

  /**
   * Sube una nueva foto de perfil para el usuario.
   * @param file El archivo de la nueva foto.
   * @returns Un observable con la respuesta del servidor.
   */
  uploadProfilePhoto(file: File): Observable<void> {
    const formData = new FormData();
    formData.append('image', file);

    return this.http.post<void>(`${BASE_URL}/me/photo`, formData);
  }

  getUsers(name?: string): Observable<UserDto[]> {
    let params = new HttpParams();
    if (name) {
      params = params.set('name', name);
    }
    return this.http.get<UserDto[]>(`${BASE_URL}/`, { params });
  }

  createUser(user: UserFormDto): Observable<UserDto> {
    return this.http.post<UserDto>(`${BASE_URL}/`, user);
  }

  createUserImage(userId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('imageFile', file);
    return this.http.post(`${BASE_URL}/${userId}/image`, formData);
  }
}
