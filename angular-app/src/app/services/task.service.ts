import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Task {
  id?: number;
  title: string;
  description: string;
  statusId: number;
  statusName?: string;
  dueDate?: string;
  priorityId: number;
  priorityName?: string;
  priorityValue?: number;
  userId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) { }

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
    console.log('Sending task to backend:', task);
    const backendTask = this.convertToBackendFormat(task);
    console.log('Task for backend:', backendTask);
    return this.http.post<any>(this.apiUrl, backendTask).pipe(
      map((createdTask) => {
        console.log('Raw response from backend:', createdTask);
        const normalized = this.normalizeTask(createdTask);
        console.log('Normalized task:', normalized);
        return normalized;
      })
    );
  }

  updateTask(task: Task): Observable<Task> {
    const backendTask = this.convertToBackendFormat(task);
    return this.http.put<any>(`${this.apiUrl}/${task.id}`, backendTask).pipe(
      map((updatedTask) => {
        const normalized = this.normalizeTask(updatedTask);
        return normalized;
      })
    );
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
  private normalizeTask(task: any): Task {
    return {
      id: task.id ? Number(task.id) : undefined,
      title: task.title || '',
      description: task.description || '',
      statusId: task.statusId || this.getStatusIdFromName(task.statusName) || 1,
      statusName: task.statusName,
      dueDate: task.dueDate || undefined,
      priorityId: task.priorityId || 3,
      priorityName: task.priorityName,
      priorityValue: task.priorityValue,
      userId: task.userId ? Number(task.userId) : undefined,
    };
  }

  /**
   * Convert backend status name to status ID
   */
  private getStatusIdFromName(statusName: string): number {
    if (!statusName) return 1;

    const name = statusName.toLowerCase();
    if (name === 'to do') return 1;
    if (name === 'in progress') return 2;
    if (name === 'review') return 3;
    if (name === 'done') return 4;
    return 1; // Default to "To Do"
  }
  /**
   * Convert frontend task format to backend format
   */
  private convertToBackendFormat(task: Task): any {
    return {
      id: task.id,
      title: task.title,
      description: task.description,
      dueDate: task.dueDate,
      priorityId: task.priorityId,
      userId: task.userId,
      statusId: task.statusId,
    };
  }
}
