import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Main dashboard component
import { DashboardComponent } from './dashboard.component';

// Child components
import { DashboardHeaderComponent } from './components/dashboard-header/dashboard-header.component';
import { DashboardSidebarComponent } from './components/dashboard-sidebar/dashboard-sidebar.component';
import { DashboardTabsComponent } from './components/dashboard-tabs/dashboard-tabs.component';
import { ApplicationCardsComponent } from './components/application-cards/application-cards.component';
import { TaskListComponent } from './components/task-list/task-list.component';
import { NotificationListComponent } from './components/notification-list/notification-list.component';
import { TabContentComponent } from './components/tab-content/tab-content.component';

@NgModule({
  declarations: [
    DashboardComponent,
    DashboardHeaderComponent,
    DashboardSidebarComponent,
    DashboardTabsComponent,
    ApplicationCardsComponent,
    TaskListComponent,
    NotificationListComponent,
    TabContentComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    DashboardComponent
  ]
})
export class DashboardModule { } 