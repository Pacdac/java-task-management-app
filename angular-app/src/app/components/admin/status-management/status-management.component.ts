import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StatusService, StatusInfo } from '../../../services/status.service';

@Component({
  selector: 'app-status-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './status-management.component.html',
  styleUrl: './status-management.component.css',
})
export class StatusManagementComponent implements OnInit {
  statuses: StatusInfo[] = [];
  editingStatus: StatusInfo | null = null;
  newStatus: Omit<StatusInfo, 'id'> = {
    name: '',
    description: '',
    color: '#cccccc',
  };
  isAdding = false;

  constructor(private statusService: StatusService) {}

  ngOnInit(): void {
    this.loadStatuses();
    // Subscribe to status changes
    this.statusService.statuses$.subscribe((statuses) => {
      this.statuses = statuses;
    });
  }

  loadStatuses(): void {
    this.statusService.loadStatuses().subscribe();
  }

  startEdit(status: StatusInfo): void {
    this.editingStatus = { ...status };
  }

  cancelEdit(): void {
    this.editingStatus = null;
  }

  saveStatus(): void {
    if (this.editingStatus) {
      this.statusService.updateStatus(this.editingStatus).subscribe({
        next: () => {
          this.editingStatus = null;
          this.loadStatuses();
        },
        error: (err) => console.error('Error updating status:', err),
      });
    }
  }

  deleteStatus(id: number): void {
    if (confirm('Are you sure you want to delete this status?')) {
      this.statusService.deleteStatus(id).subscribe({
        next: () => this.loadStatuses(),
        error: (err) => console.error('Error deleting status:', err),
      });
    }
  }

  toggleAddForm(): void {
    this.isAdding = !this.isAdding;
    if (!this.isAdding) {
      this.resetNewStatus();
    }
  }

  addStatus(): void {
    this.statusService.createStatus(this.newStatus).subscribe({
      next: () => {
        this.isAdding = false;
        this.resetNewStatus();
        this.loadStatuses();
      },
      error: (err) => console.error('Error creating status:', err),
    });
  }

  resetNewStatus(): void {
    this.newStatus = {
      name: '',
      description: '',
      color: '#cccccc',
    };
  }
}
