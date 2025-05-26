import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PriorityService, Priority } from '../../../services/priority.service';

@Component({
  selector: 'app-priority-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './priority-management.component.html',
  styleUrl: './priority-management.component.css',
})
export class PriorityManagementComponent implements OnInit {
  priorities: Priority[] = [];
  editingPriority: Priority | null = null;
  newPriority: Omit<Priority, 'id'> = {
    name: '',
    value: 0,
    description: '',
    color: '#cccccc',
    displayOrder: 0,
  };
  isAdding = false;

  constructor(private priorityService: PriorityService) {}

  ngOnInit(): void {
    this.loadPriorities();
    // Subscribe to priority changes
    this.priorityService.priorities$.subscribe((priorities) => {
      this.priorities = priorities;
    });
  }

  loadPriorities(): void {
    this.priorityService.loadPriorities().subscribe();
  }

  startEdit(priority: Priority): void {
    this.editingPriority = { ...priority };
  }

  cancelEdit(): void {
    this.editingPriority = null;
  }

  savePriority(): void {
    if (this.editingPriority) {
      this.priorityService.updatePriority(this.editingPriority).subscribe({
        next: () => {
          this.editingPriority = null;
          this.loadPriorities();
        },
        error: (err) => console.error('Error updating priority:', err),
      });
    }
  }

  deletePriority(id: number): void {
    if (confirm('Are you sure you want to delete this priority?')) {
      this.priorityService.deletePriority(id).subscribe({
        next: () => this.loadPriorities(),
        error: (err) => console.error('Error deleting priority:', err),
      });
    }
  }

  toggleAddForm(): void {
    this.isAdding = !this.isAdding;
    if (!this.isAdding) {
      this.resetNewPriority();
    }
  }

  addPriority(): void {
    this.priorityService.createPriority(this.newPriority).subscribe({
      next: () => {
        this.isAdding = false;
        this.resetNewPriority();
        this.loadPriorities();
      },
      error: (err) => console.error('Error creating priority:', err),
    });
  }

  resetNewPriority(): void {
    this.newPriority = {
      name: '',
      value: 0,
      description: '',
      color: '#cccccc',
      displayOrder: 0,
    };
  }
}
