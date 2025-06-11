import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../../services/task.service';
import { StatusService } from '../../../services/status.service';
import { PriorityService } from '../../../services/priority.service';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css',
})
export class TaskFormComponent implements OnInit {
  @Input() mode: 'create' | 'edit' = 'create';
  @Input() task: Task | Partial<Task> = {
    title: '',
    description: '',
    statusId: 1,
    priorityId: 3,
  };

  @Output() save = new EventEmitter<Task>();
  @Output() close = new EventEmitter<void>();

  @Input() error = '';
  @Input() loading = false;

  constructor(
    public statusService: StatusService,
    public priorityService: PriorityService
  ) { }

  ngOnInit() {
    this.statusService.loadStatuses().subscribe();
    this.priorityService.loadPriorities().subscribe();
  }
  saveTask(): void {
    if (!this.task.title || !this.task.description) {
      return;
    }

    if (this.task.dueDate && typeof this.task.dueDate === 'string') {
      const date = new Date(this.task.dueDate);
      if (!isNaN(date.getTime())) {
        this.task.dueDate = date.toISOString().split('T')[0];
      }
    }

    const taskToSave: Task = {
      ...this.task,
      statusId: Number(this.task.statusId) || 1,
      priorityId: Number(this.task.priorityId) || 3,
    } as Task;

    this.save.emit(taskToSave);
  }
  onCancel(): void {
    this.close.emit();
  }

  get isEditing(): boolean {
    return this.mode === 'edit';
  }
}
