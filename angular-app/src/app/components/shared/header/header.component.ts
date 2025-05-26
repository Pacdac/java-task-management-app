import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  isAuthenticated = false;
  isAdmin = false;
  username: string | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.isAuthenticated$.subscribe((isAuth) => {
      this.isAuthenticated = isAuth;
      // Check admin status whenever authentication changes
      this.isAdmin = this.authService.isAdmin();
      console.log('User authentication status:', this.isAuthenticated);
      console.log('User admin status:', this.isAdmin);
    });

    this.authService.currentUser$.subscribe((user) => {
      this.username = user?.username || null;
    });
  }

  logout(event: Event): void {
    event.preventDefault();
    this.authService.logout();
  }
}
