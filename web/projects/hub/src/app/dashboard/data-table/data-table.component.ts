import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface TableData {
  id: string;
  name: string;
  email: string;
  role: string;
  status: 'active' | 'inactive' | 'pending';
  lastLogin: string;
  actions?: string[];
}

/**
 * Data Table Component
 * 
 * Displays tabular data with sorting, filtering, and pagination.
 * 
 * @component DataTableComponent
 */
@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="data-table-container">
      <div class="table-header">
        <h3 class="table-title">Recent Users</h3>
        <div class="table-actions">
          <div class="search-box">
            <input type="text" placeholder="Search users..." 
                   [(ngModel)]="searchTerm" 
                   (input)="onSearch()"
                   class="search-input">
          </div>
          <button class="btn btn-primary">
            <i class="icon-plus"></i>
            Add User
          </button>
        </div>
      </div>

      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th *ngFor="let column of columns" 
                  [class.sortable]="column.sortable"
                  (click)="onSort(column.key)">
                {{ column.label }}
                <i class="sort-icon" 
                   *ngIf="column.sortable && sortColumn === column.key"
                   [class]="getSortIcon()"></i>
              </th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of paginatedData" class="table-row">
              <td>{{ item.name }}</td>
              <td>{{ item.email }}</td>
              <td>{{ item.role }}</td>
              <td>
                <span class="status-badge" [class]="'status-' + item.status">
                  {{ item.status | titlecase }}
                </span>
              </td>
              <td>{{ formatDate(item.lastLogin) }}</td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn edit-btn" (click)="onEdit(item)">
                    <i class="icon-edit"></i>
                  </button>
                  <button class="action-btn delete-btn" (click)="onDelete(item)">
                    <i class="icon-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-footer">
        <div class="pagination-info">
          Showing {{ getStartIndex() + 1 }} to {{ getEndIndex() }} of {{ filteredData.length }} entries
        </div>
        <div class="pagination">
          <button class="pagination-btn" 
                  [disabled]="currentPage === 1"
                  (click)="onPageChange(currentPage - 1)">
            Previous
          </button>
          
          <button *ngFor="let page of getPageNumbers()" 
                  class="pagination-btn"
                  [class.active]="page === currentPage"
                  (click)="onPageChange(page)">
            {{ page }}
          </button>
          
          <button class="pagination-btn" 
                  [disabled]="currentPage === getTotalPages()"
                  (click)="onPageChange(currentPage + 1)">
            Next
          </button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent implements OnInit {
  searchTerm: string = '';
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  currentPage: number = 1;
  itemsPerPage: number = 10;

  columns = [
    { key: 'name', label: 'Name', sortable: true },
    { key: 'email', label: 'Email', sortable: true },
    { key: 'role', label: 'Role', sortable: true },
    { key: 'status', label: 'Status', sortable: true },
    { key: 'lastLogin', label: 'Last Login', sortable: true }
  ];

  data: TableData[] = [
    {
      id: '1',
      name: 'John Doe',
      email: 'john.doe@example.com',
      role: 'Admin',
      status: 'active',
      lastLogin: '2024-01-15T10:30:00Z'
    },
    {
      id: '2',
      name: 'Jane Smith',
      email: 'jane.smith@example.com',
      role: 'User',
      status: 'active',
      lastLogin: '2024-01-14T15:45:00Z'
    },
    {
      id: '3',
      name: 'Bob Johnson',
      email: 'bob.johnson@example.com',
      role: 'Moderator',
      status: 'inactive',
      lastLogin: '2024-01-10T09:15:00Z'
    },
    {
      id: '4',
      name: 'Alice Brown',
      email: 'alice.brown@example.com',
      role: 'User',
      status: 'pending',
      lastLogin: '2024-01-12T14:20:00Z'
    },
    {
      id: '5',
      name: 'Charlie Wilson',
      email: 'charlie.wilson@example.com',
      role: 'Admin',
      status: 'active',
      lastLogin: '2024-01-16T11:00:00Z'
    }
  ];

  filteredData: TableData[] = [];
  paginatedData: TableData[] = [];

  ngOnInit(): void {
    this.filteredData = [...this.data];
    this.updatePaginatedData();
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredData = [...this.data];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredData = this.data.filter(item =>
        item.name.toLowerCase().includes(term) ||
        item.email.toLowerCase().includes(term) ||
        item.role.toLowerCase().includes(term)
      );
    }
    this.currentPage = 1;
    this.updatePaginatedData();
  }

  onSort(column: string): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }

    this.filteredData.sort((a, b) => {
      const aValue = this.getColumnValue(a, column);
      const bValue = this.getColumnValue(b, column);

      if (aValue === undefined || bValue === undefined) {
        return 0;
      }

      let comparison = 0;
      if (aValue < bValue) {
        comparison = -1;
      } else if (aValue > bValue) {
        comparison = 1;
      }

      return this.sortDirection === 'desc' ? -comparison : comparison;
    });

    this.updatePaginatedData();
  }

  private getColumnValue(item: TableData, column: string): any {
    switch (column) {
      case 'name':
        return item.name;
      case 'email':
        return item.email;
      case 'role':
        return item.role;
      case 'status':
        return item.status;
      case 'lastLogin':
        return new Date(item.lastLogin);
      default:
        return '';
    }
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updatePaginatedData();
  }

  private updatePaginatedData(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.filteredData.slice(startIndex, endIndex);
  }

  getTotalPages(): number {
    return Math.ceil(this.filteredData.length / this.itemsPerPage);
  }

  getPageNumbers(): number[] {
    const totalPages = this.getTotalPages();
    const pages: number[] = [];
    
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  getStartIndex(): number {
    return (this.currentPage - 1) * this.itemsPerPage;
  }

  getEndIndex(): number {
    const endIndex = this.currentPage * this.itemsPerPage;
    return Math.min(endIndex, this.filteredData.length);
  }

  getSortIcon(): string {
    return this.sortDirection === 'asc' ? 'icon-arrow-up' : 'icon-arrow-down';
  }

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return dateString;
    }
  }

  onEdit(item: TableData): void {
    console.log('Edit item:', item);
    // Implement edit functionality
  }

  onDelete(item: TableData): void {
    console.log('Delete item:', item);
    // Implement delete functionality
  }
}