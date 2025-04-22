import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from '../dtos/user.dto';

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


  /**
   * Actualiza la información del usuario.
   * @param user Los datos actualizados del usuario.
   * @returns Un observable con la respuesta del servidor.
   */
  updateUser(user: Partial<UserDto>): Observable<UserDto> {
    return this.http.put<UserDto>(`${BASE_URL}/me`, user);
  }

  /**
   * Cambia la contraseña del usuario.
   * @param currentPassword La contraseña actual.
   * @param newPassword La nueva contraseña.
   * @returns Un observable con la respuesta del servidor.
   */
  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${BASE_URL}/me/change-password`, {
      currentPassword,
      newPassword
    });
  }

  /**
   * Elimina la cuenta del usuario.
   * @returns Un observable con la respuesta del servidor.
   */
  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`, {
      withCredentials: true
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
}
