import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Task {
  title: string;
  tag: string;
  priority: 'high' | 'medium' | 'low';
  due: string;
  assigned: string;
  actions: string[];
}

@Component({
  selector: 'app-task-list',
  standalone: false,
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent {
  @Input() tasks: Task[] = [];
  @Input() showViewAll: boolean = true;
  @Input() title: string = 'My Tasks';
  
  @Output() viewAll = new EventEmitter<void>();
  @Output() taskAction = new EventEmitter<{task: Task, action: string}>();

  onViewAll(): void {
    this.viewAll.emit();
  }

  onTaskAction(task: Task, action: string): void {
    this.taskAction.emit({ task, action });
  }
} 