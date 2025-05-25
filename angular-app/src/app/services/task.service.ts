import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Task {
  id?: number;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  dueDate?: string;
  priority: number;
  userId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}
  getTasks(): Observable<Task[]> {
    return this.http
      .get<any[]>(this.apiUrl)
      .pipe(map((tasks) => tasks.map((task) => this.normalizeTask(task))));
  }
  getTaskById(id: number): Observable<Task> {
    return this.http
      .get<any>(`${this.apiUrl}/${id}`)
      .pipe(map((task) => this.normalizeTask(task)));
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${task.id}`, task);
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  private normalizeTask(task: any): Task {
    // Ensure all required properties have proper types
    return {
      id: task.id ? Number(task.id) : undefined,
      title: task.title || '',
      description: task.description || '',
      status: this.normalizeStatus(task.status),
      dueDate: task.dueDate || undefined,
      priority: task.priority,
      userId: task.userId ? Number(task.userId) : undefined,
    };
  }

  private normalizeStatus(status: any): 'TODO' | 'IN_PROGRESS' | 'DONE' {
    if (!status) return 'TODO';

    const statusStr = (status + '').toUpperCase();
    if (statusStr === 'IN_PROGRESS' || statusStr === 'IN PROGRESS')
      return 'IN_PROGRESS';
    if (statusStr === 'DONE' || statusStr === 'COMPLETED') return 'DONE';
    return 'TODO'; // Default
  }
}
