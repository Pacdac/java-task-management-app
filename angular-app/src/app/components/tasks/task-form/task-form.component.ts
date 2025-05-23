import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../../services/task.service';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css',
})
export class TaskFormComponent {
  @Input() mode: 'create' | 'edit' = 'create';
  @Input() task: Task | Partial<Task> = {
    title: '',
    description: '',
    status: 'TODO',
    priority: 'MEDIUM',
  };

  @Output() save = new EventEmitter<Task>();
  @Output() close = new EventEmitter<void>();

  @Input() error = '';
  @Input() loading = false;
  saveTask(): void {
    if (!this.task.title || !this.task.description) {
      return; // Let parent component handle validation
    }

    // Format date to ISO format if it exists
    if (this.task.dueDate && typeof this.task.dueDate === 'string') {
      const date = new Date(this.task.dueDate);
      if (!isNaN(date.getTime())) {
        this.task.dueDate = date.toISOString().split('T')[0];
      }
    }

    this.save.emit(this.task as Task);
  }
  onCancel(): void {
    this.close.emit();
  }

  get isEditing(): boolean {
    return this.mode === 'edit';
  }
}
