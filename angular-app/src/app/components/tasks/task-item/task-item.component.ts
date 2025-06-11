import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../../services/task.service';
import { StatusService } from '../../../services/status.service';
import { PriorityService } from '../../../services/priority.service';

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
    statusId: number;
  }>();
  @Output() editTask = new EventEmitter<Task>();
  @Output() deleteTask = new EventEmitter<number | undefined>();

  isEditing = false;
  editForm: Task = {} as Task;

  constructor(
    public statusService: StatusService,
    public priorityService: PriorityService
  ) { }

  getPriorityString(priorityId: number): string {
    const priority = this.priorityService.getPriorityById(priorityId);
    return priority ? priority.name.toLowerCase() : 'unknown';
  }

  getStatusClass(statusId: number): string {
    const status = this.statusService.getStatusById(statusId);
    return status ? this.statusService.getStatusCssClass(status.name) : '';
  }

  formatStatus(statusId: number): string {
    return this.statusService.getStatusName(statusId);
  }

  formatPriority(priorityId: number): string {
    const priority = this.priorityService.getPriorityById(priorityId);
    return priority ? priority.name : '';
  }
  onEdit(): void {
    this.isEditing = true;
    this.editForm = {
      ...this.task,
      dueDate: this.task.dueDate
        ? new Date(this.task.dueDate).toISOString().split('T')[0]
        : '',
      statusId: this.task.statusId || 1,
      priorityId: Number(this.task.priorityId) || 3,
    };
  }

  onCancelEdit(): void {
    this.isEditing = false;
    this.editForm = {} as Task;
  }

  onSaveEdit(): void {
    if (this.editForm.title.trim() && this.editForm.description.trim()) {
      const updatedTask: Task = {
        ...this.editForm,
        dueDate: this.editForm.dueDate || undefined,
        statusId: Number(this.editForm.statusId) || 1,
        priorityId: Number(this.editForm.priorityId) || 3,
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
