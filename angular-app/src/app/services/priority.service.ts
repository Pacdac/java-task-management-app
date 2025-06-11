import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Priority {
  id: number;
  name: string;
  value: number;
  description?: string;
  color?: string;
  displayOrder: number;
}

@Injectable({
  providedIn: 'root',
})
export class PriorityService {
  private apiUrl = 'http://localhost:8080/api/task-priorities';
  private prioritiesSubject = new BehaviorSubject<Priority[]>([]);
  public priorities$ = this.prioritiesSubject.asObservable();

  // Fallback priorities for when API is not available
  private readonly fallbackPriorities: Priority[] = [
    {
      id: 1,
      name: 'Low',
      value: 1,
      description: 'Low priority tasks',
      color: '#3498db',
      displayOrder: 1,
    },
    {
      id: 2,
      name: 'Medium',
      value: 2,
      description: 'Medium priority tasks',
      color: '#f39c12',
      displayOrder: 2,
    },
    {
      id: 3,
      name: 'High',
      value: 3,
      description: 'High priority tasks',
      color: '#e74c3c',
      displayOrder: 3,
    },
  ];
  constructor(private http: HttpClient) {
    this.loadPriorities().subscribe(
      () => { },
      (error) => console.error('Error loading priorities:', error)
    );
  }
  /**
   * Load all priorities from the backend
   */
  loadPriorities(): Observable<Priority[]> {
    return this.http.get<Priority[]>(this.apiUrl).pipe(
      map((priorities) => {
        this.prioritiesSubject.next(priorities);
        return priorities;
      })
    );
  }

  /**
   * Get all priorities (from cache or fallback)
   */
  getAllPriorities(): Priority[] {
    const priorities = this.prioritiesSubject.value;
    return priorities.length > 0 ? priorities : this.fallbackPriorities;
  }

  /**
   * Get priority by ID
   */
  getPriorityById(id: number): Priority | undefined {
    return this.getAllPriorities().find((priority) => priority.id === id);
  }

  /**
   * Get priority by value
   */
  getPriorityByValue(value: number): Priority | undefined {
    return this.getAllPriorities().find((priority) => priority.value === value);
  }

  /**
   * Get priority name by ID
   */
  getPriorityName(id: number): string {
    const priority = this.getPriorityById(id);
    return priority ? priority.name : 'Unknown';
  }

  /**
   * Get priority value by ID
   */
  getPriorityValue(id: number): number {
    const priority = this.getPriorityById(id);
    return priority ? priority.value : 1;
  }

  /**
   * Get CSS class for a priority name
   */
  getPriorityCssClass(priorityName: string): string {
    const name = priorityName.toLowerCase().replace(/\s+/g, '-');
    return `priority-${name}`;
  }

  /**
   * Get CSS class for priority
   */
  getPriorityClass(id: number): string {
    const priority = this.getPriorityById(id);
    return priority ? this.getPriorityCssClass(priority.name) : '';
  }

  /**
   * Create a new priority (admin only)
   */
  createPriority(priority: Omit<Priority, 'id'>): Observable<Priority> {
    return this.http.post<Priority>(this.apiUrl, priority).pipe(
      map((newPriority) => {
        this.loadPriorities().subscribe();
        return newPriority;
      })
    );
  }

  /**
   * Update a priority (admin only)
   */
  updatePriority(priority: Priority): Observable<Priority> {
    return this.http
      .put<Priority>(`${this.apiUrl}/${priority.id}`, priority)
      .pipe(
        map((updatedPriority) => {
          this.loadPriorities().subscribe();
          return updatedPriority;
        })
      );
  }

  /**
   * Delete a priority (admin only)
   */
  deletePriority(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      map(() => {
        this.loadPriorities().subscribe();
      })
    );
  }

  /**
   * Get the default priority ID (Medium)
   */
  getDefaultPriorityId(): number {
    return 2;
  }
}
