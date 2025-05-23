import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../../services/task.service';

@Component({
  selector: 'app-task-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-item.component.html',
  styleUrl: './task-item.component.css',
})
export class TaskItemComponent {
  @Input() task!: Task;
  @Output() statusChange = new EventEmitter<{
    task: Task;
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  }>();
  @Output() editTask = new EventEmitter<Task>();
  @Output() deleteTask = new EventEmitter<number | undefined>();

  // Helper methods for template
  getSafePriorityClass(priority: any): string {
    if (!priority) return 'low';
    return (priority + '').toLowerCase();
  }

  getSafeStatusClass(status: any): string {
    if (!status) return 'todo';
    return (status + '').toLowerCase();
  }

  formatStatus(status: any): string {
    if (!status) return '';
    const statusStr = status + '';
    return statusStr.replace ? statusStr.replace('_', ' ') : statusStr;
  }

  onStatusChange(newStatus: 'TODO' | 'IN_PROGRESS' | 'DONE'): void {
    this.statusChange.emit({ task: this.task, status: newStatus });
  }

  onEdit(): void {
    this.editTask.emit(this.task);
  }

  onDelete(): void {
    this.deleteTask.emit(this.task.id);
  }
}
