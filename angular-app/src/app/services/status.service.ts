import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

export interface StatusInfo {
  id: number;
  name: string;
  description?: string;
  color?: string;
}

// Legacy interface for backward compatibility
export interface StatusInfoLegacy {
  id: number;
  name: string;
  label: string;
  cssClass: string;
}

@Injectable({
  providedIn: 'root',
})
export class StatusService {
  private apiUrl = 'http://localhost:8080/api/task-statuses';
  private statusesSubject = new BehaviorSubject<StatusInfo[]>([]);
  public statuses$ = this.statusesSubject.asObservable();

  // Fallback statuses for when API is not available
  private readonly fallbackStatuses: StatusInfo[] = [
    {
      id: 1,
      name: 'To Do',
      description: 'Tasks that need to be started',
      color: '#3498db',
    },
    {
      id: 2,
      name: 'In Progress',
      description: 'Tasks currently being worked on',
      color: '#f39c12',
    },
    {
      id: 3,
      name: 'Review',
      description: 'Tasks that need review',
      color: '#9b59b6',
    },
    { id: 4, name: 'Done', description: 'Completed tasks', color: '#2ecc71' },
  ];
  constructor(private http: HttpClient) {
    this.loadStatuses().subscribe(
      (statuses) =>
        console.log('Statuses loaded in constructor:', statuses.length),
      (error) => console.error('Error loading statuses:', error)
    );
  }
  /**
   * Load all statuses from the backend
   */
  loadStatuses(): Observable<StatusInfo[]> {
    return this.http.get<StatusInfo[]>(this.apiUrl).pipe(
      map((statuses) => {
        this.statusesSubject.next(statuses);
        return statuses;
      })
    );
  }
  /**
   * Get all available statuses (from cache or fallback)
   */
  getAllStatuses(): StatusInfo[] {
    const statuses = this.statusesSubject.value;
    if (statuses.length > 0) {
      return statuses;
    } else {
      this.statusesSubject.next(this.fallbackStatuses);
      return this.fallbackStatuses;
    }
  }

  /**
   * Get all statuses with legacy format for backward compatibility
   */
  getAllStatusesLegacy(): StatusInfoLegacy[] {
    return this.getAllStatuses().map((status) => ({
      id: status.id,
      name: status.name,
      label: status.name,
      cssClass: this.getStatusCssClass(status.name),
    }));
  }

  /**
   * Get status info by ID
   */
  getStatusById(id: number): StatusInfo | undefined {
    return this.getAllStatuses().find((status) => status.id === id);
  }

  /**
   * Get status name by ID
   */
  getStatusName(id: number): string {
    const status = this.getStatusById(id);
    return status ? status.name : 'Unknown';
  }

  /**
   * Get status label by ID (same as name for backward compatibility)
   */
  getStatusLabel(id: number): string {
    return this.getStatusName(id);
  }

  /**
   * Get CSS class for a status name
   */
  getStatusCssClass(statusName: string): string {
    const name = statusName.toLowerCase().replace(/\s+/g, '-');
    return `status-${name}`;
  }

  /**
   * Create a new status (admin only)
   */
  createStatus(status: Omit<StatusInfo, 'id'>): Observable<StatusInfo> {
    return this.http.post<StatusInfo>(this.apiUrl, status).pipe(
      map((newStatus) => {
        this.loadStatuses().subscribe();
        return newStatus;
      })
    );
  }

  /**
   * Update a status (admin only)
   */
  updateStatus(status: StatusInfo): Observable<StatusInfo> {
    return this.http
      .put<StatusInfo>(`${this.apiUrl}/${status.id}`, status)
      .pipe(
        map((updatedStatus) => {
          this.loadStatuses().subscribe();
          return updatedStatus;
        })
      );
  }

  /**
   * Delete a status (admin only)
   */
  deleteStatus(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      map(() => {
        this.loadStatuses().subscribe();
      })
    );
  }

  /**
   * Get CSS class for status
   */
  getStatusClass(id: number): string {
    const status = this.getStatusById(id);
    return status ? this.getStatusCssClass(status.name) : '';
  }

  /**
   * Get the default status ID (To Do)
   */
  getDefaultStatusId(): number {
    return 1;
  }
}
