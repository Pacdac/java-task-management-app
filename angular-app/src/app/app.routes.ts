import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { TaskListComponent } from './components/tasks/task-list/task-list.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { PriorityManagementComponent } from './components/admin/priority-management/priority-management.component';
import { StatusManagementComponent } from './components/admin/status-management/status-management.component';
import { UserManagementComponent } from './components/admin/user-management/user-management.component';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'auth',
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
    ],
  },
  {
    path: 'tasks',
    component: TaskListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'priorities', component: PriorityManagementComponent },
      { path: 'statuses', component: StatusManagementComponent },
      { path: 'users', component: UserManagementComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];
