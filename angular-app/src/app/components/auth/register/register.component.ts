import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  };

  loading = false;
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    // Validate form
    if (
      !this.registerForm.username ||
      !this.registerForm.email ||
      !this.registerForm.password ||
      !this.registerForm.confirmPassword
    ) {
      this.error = 'All fields are required';
      return;
    }

    if (this.registerForm.password !== this.registerForm.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.loading = true;
    this.error = '';

    // Extract data for API request (exclude confirmPassword)
    const registrationData = {
      username: this.registerForm.username,
      email: this.registerForm.email,
      password: this.registerForm.password,
    };

    this.authService.register(registrationData).subscribe({
      next: () => {
        // Navigate to tasks page on successful registration
        this.router.navigate(['/tasks']);
      },
      error: (error) => {
        this.error =
          error.error?.message || 'Registration failed. Please try again.';
        this.loading = false;
      },
    });
  }
}
