import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-rename-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rename-dialog.component.html',
  styleUrl: './rename-dialog.component.scss'
})
export class RenameDialogComponent implements OnInit {
  @Input() currentName: string = '';
  @Input() visible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<string>();

  newName: string = '';

  ngOnInit(): void {
    this.newName = this.currentName;
  }

  onSave(): void {
    if (this.newName.trim()) {
      this.save.emit(this.newName.trim());
    }
  }

  onCancel(): void {
    this.newName = this.currentName;
    this.close.emit();
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.onSave();
    } else if (event.key === 'Escape') {
      this.onCancel();
    }
  }
}
