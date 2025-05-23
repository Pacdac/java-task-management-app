import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService, Task } from '../../../services/task.service';
import { Subscription } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { TaskItemComponent } from '../task-item/task-item.component';
import { TaskFormComponent } from '../task-form/task-form.component';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskItemComponent, TaskFormComponent],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css',
})
export class TaskListComponent implements OnInit, OnDestroy {
  tasks: Task[] = [];
  loading = false;
  error = '';
  private taskSubscription: Subscription | undefined;

  // Form for new task
  newTask: Partial<Task> = {
    title: '',
    description: '',
    status: 'TODO',
    priority: 'MEDIUM',
  };
  // For task editing and creation
  taskForEdit: Task | null = null;
  showTaskForm = false;
  formMode: 'create' | 'edit' = 'create';

  // For task filtering
  filterStatus: string = '';
  filterPriority: string = '';
  searchTerm: string = '';

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  ngOnDestroy(): void {
    if (this.taskSubscription) {
      this.taskSubscription.unsubscribe();
    }
  }

  loadTasks(): void {
    this.loading = true;
    this.error = '';
    this.taskSubscription = this.taskService
      .getTasks()
      .pipe(
        catchError((err) => {
          this.error =
            err.error?.message ||
            'Failed to load tasks. Please try again later.';
          console.error('Error loading tasks:', err);
          return []; // Return empty array on error
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (tasks) => {
          this.tasks = tasks;
          console.log('Tasks loaded successfully:', tasks);
        },
        error: (err) => {
          this.tasks = []; // Reset tasks array on error
          console.error('Error in task subscription:', err);
        },
      });
  }
  onTaskStatusChange(event: {
    task: Task;
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  }): void {
    const { task, status } = event;
    // Store original status in case of error
    const originalStatus = task.status;

    // Update UI immediately for responsive feel
    task.status = status;

    const updatedTask: Task = { ...task, status };

    this.taskService
      .updateTask(updatedTask)
      .pipe(
        catchError((err) => {
          this.error =
            err.error?.message ||
            'Failed to update task. Please try again later.';
          console.error('Error updating task:', err);
          // Revert the status change in the UI
          task.status = originalStatus;
          throw err;
        })
      )
      .subscribe((result) => {
        // Update the local task with returned data
        const index = this.tasks.findIndex((t) => t.id === result.id);
        if (index !== -1) {
          this.tasks[index] = result;
        }
      });
  }
  onDeleteTask(id: number | undefined): void {
    if (!id) {
      this.error = 'Task ID is required';
      return;
    }
    if (!confirm('Are you sure you want to delete this task?')) {
      return;
    }

    this.loading = true;
    this.taskService
      .deleteTask(id)
      .pipe(
        catchError((err) => {
          this.error =
            err.error?.message ||
            'Failed to delete task. Please try again later.';
          console.error('Error deleting task:', err);
          throw err;
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(() => {
        this.tasks = this.tasks.filter((task) => task.id !== id);
      });
  }
  get filteredTasks(): Task[] {
    return this.tasks.filter((task) => {
      // Filter by status
      if (
        this.filterStatus &&
        (task.status || '').toString() !== this.filterStatus
      ) {
        return false;
      }

      // Filter by priority
      if (
        this.filterPriority &&
        (task.priority || '').toString() !== this.filterPriority
      ) {
        return false;
      }

      // Filter by search term
      if (this.searchTerm) {
        const searchTerm = this.searchTerm.toLowerCase();
        const title = (task.title || '').toString().toLowerCase();
        const description = (task.description || '').toString().toLowerCase();

        if (!title.includes(searchTerm) && !description.includes(searchTerm)) {
          return false;
        }
      }

      return true;
    });
  }
  openCreateTaskForm(): void {
    this.taskForEdit = {
      title: '',
      description: '',
      status: 'TODO',
      priority: 'MEDIUM',
    };
    this.formMode = 'create';
    this.showTaskForm = true;
  }

  openEditTaskForm(task: Task): void {
    this.taskForEdit = { ...task };
    this.formMode = 'edit';
    this.showTaskForm = true;
  }

  closeTaskForm(): void {
    this.showTaskForm = false;
    this.taskForEdit = null;
    this.error = '';
  }

  saveTask(task: Task): void {
    if (!task.title || !task.description) {
      this.error = 'Title and description are required';
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.formMode === 'create') {
      this.createNewTask(task);
    } else {
      this.updateExistingTask(task);
    }
  }

  private createNewTask(task: Task): void {
    this.taskService
      .createTask(task)
      .pipe(
        catchError((err) => {
          this.error =
            err.error?.message ||
            'Failed to create task. Please try again later.';
          console.error('Error creating task:', err);
          throw err;
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((createdTask) => {
        this.tasks.push(createdTask);
        this.closeTaskForm();
      });
  }

  private updateExistingTask(task: Task): void {
    this.taskService
      .updateTask(task)
      .pipe(
        catchError((err) => {
          this.error =
            err.error?.message ||
            'Failed to update task. Please try again later.';
          console.error('Error updating task:', err);
          throw err;
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((updatedTask) => {
        // Update the local task with returned data
        const index = this.tasks.findIndex((t) => t.id === updatedTask.id);
        if (index !== -1) {
          this.tasks[index] = updatedTask;
        }
        this.closeTaskForm();
      });
  }
}
