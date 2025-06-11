import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css',
})
export class ProfilePageComponent implements OnInit {
  user: User | null = null;
  editMode = false;
  formData: Partial<User> = {};
  errorMessage = '';
  successMessage = '';
  loading = true;

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.loading = true;
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.resetForm();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading user profile:', err);
        this.errorMessage = 'Failed to load your profile. Please try again.';
        this.loading = false;
      },
    });
  }

  toggleEditMode(): void {
    this.editMode = !this.editMode;
    if (!this.editMode) {
      this.resetForm();
    }
  }
  resetForm(): void {
    if (this.user) {
      this.formData = {
        username: this.user.username,
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        email: this.user.email,
        role: this.user.role,
        password: '',
      };
    }
    this.errorMessage = '';
    this.successMessage = '';
  }
  updateProfile(): void {
    if (!this.formData.email) {
      this.errorMessage = 'Email is required';
      return;
    }

    const updateData = { ...this.formData };

    if (!updateData.username && this.user) {
      updateData.username = this.user.username;
    }

    if (!updateData.password) {
      delete updateData.password;
    }

    this.loading = true;
    console.log('Updating profile with data:', updateData);
    this.userService.updateCurrentUser(updateData).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.successMessage = 'Profile updated successfully';
        this.errorMessage = '';
        this.loading = false;
        this.editMode = false;

        const currentUser = this.authService.getCurrentUser();
        if (currentUser?.email !== updatedUser.email) {
          this.authService.updateUserInfo(updatedUser);
        }
      },
      error: (err) => {
        console.error('Error updating profile:', err);
        this.errorMessage =
          err.error?.message || 'Failed to update profile. Please try again.';
        this.loading = false;
      },
    });
  }

  isAdmin(): boolean {
    return this.user?.role === 'ADMIN';
  }
}
