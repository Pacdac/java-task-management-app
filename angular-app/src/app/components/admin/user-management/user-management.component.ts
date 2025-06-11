import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService, User } from '../../../services/user.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css',
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  editingUser: User | null = null;
  originalUser: User | null = null;
  isLoading = false;
  error: string | null = null;
  availableRoles = ['USER', 'ADMIN'];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.error = null;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.error = 'Failed to load users. Please try again.';
        this.isLoading = false;
      },
    });
  }

  startEdit(user: User): void {
    this.originalUser = { ...user };

    this.editingUser = {
      ...user,
      role: user.role?.replace('ROLE_', '') || 'USER',
    };
  }

  cancelEdit(): void {
    this.editingUser = null;
    this.originalUser = null;
  }

  hasChanges(): boolean {
    if (!this.editingUser || !this.originalUser) return false;

    const normalizedOriginalRole = this.originalUser.role.replace('ROLE_', '');
    const normalizedEditingRole = this.editingUser.role.replace('ROLE_', '');

    return (
      this.editingUser.email !== this.originalUser.email ||
      normalizedEditingRole !== normalizedOriginalRole
    );
  }

  saveUser(): void {
    if (!this.editingUser) return;

    if (!this.hasChanges()) {
      this.editingUser = null;
      this.originalUser = null;
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.userService.updateUser(this.editingUser).subscribe({
      next: () => {
        this.loadUsers();
        this.editingUser = null;
        this.originalUser = null;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating user:', err);
        this.error = 'Failed to update user. Please try again.';
        this.isLoading = false;
      },
    });
  }

  deleteUser(id: number): void {
    if (
      !confirm(
        'Are you sure you want to delete this user? This action cannot be undone.'
      )
    ) {
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.loadUsers();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error deleting user:', err);
        this.error = 'Failed to delete user. Please try again.';
        this.isLoading = false;
      },
    });
  }

  getRoleDisplayName(role: string): string {
    return role.replace('ROLE_', '');
  }
}
