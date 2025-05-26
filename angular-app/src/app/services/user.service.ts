import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface User {
  id: number;
  username: string;
  email: string;
  role: string; // e.g., 'USER', 'ADMIN'
  createdAt: string;
  lastLogin?: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}
  /**
   * Get all users (admin only)
   */ getAllUsers(): Observable<User[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map((response) => {
        // Handle different response formats
        let users: User[];

        if (Array.isArray(response)) {
          users = response;
        } else if (
          response &&
          response.content &&
          Array.isArray(response.content)
        ) {
          // Handle paginated response
          users = response.content;
        } else if (response && typeof response === 'object') {
          // Handle single user wrapped in object
          users = [response];
        } else {
          console.error('Unexpected API response format:', response);
          users = [];
        }

        return users;
      })
    );
  }

  /**
   * Get user by ID (admin only)
   */
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  /**
   * Update user (admin only)
   * Only non-sensitive fields can be updated (email, display name, etc.)
   */
  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${user.id}`, user);
  }

  /**
   * Update user roles (admin only)
   */
  updateUserRoles(userId: number, roles: string[]): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${userId}/roles`, { roles });
  }

  /**
   * Delete user (admin only)
   */
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
