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

  // Edit mode state
  isEditing = false;
  editForm: Task = {} as Task;

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

  formatPriority(priority: any): string {
    if (!priority) return '';
    const priorityStr = priority + '';
    return (
      priorityStr.charAt(0).toUpperCase() + priorityStr.slice(1).toLowerCase()
    );
  }

  onEdit(): void {
    this.isEditing = true;
    // Create a copy of the task for editing
    this.editForm = {
      ...this.task,
      dueDate: this.task.dueDate
        ? new Date(this.task.dueDate).toISOString().split('T')[0]
        : '',
    };
  }

  onCancelEdit(): void {
    this.isEditing = false;
    this.editForm = {} as Task;
  }

  onSaveEdit(): void {
    if (this.editForm.title.trim() && this.editForm.description.trim()) {
      // Convert date back to proper format if needed
      const updatedTask = {
        ...this.editForm,
        dueDate: this.editForm.dueDate || undefined,
      };
      this.editTask.emit(updatedTask);
      this.isEditing = false;
    }
  }

  onDelete(): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.deleteTask.emit(this.task.id);
    }
  }
}
