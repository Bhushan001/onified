import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DragState } from '../models/canvas.interface';

@Injectable({
  providedIn: 'root'
})
export class DragDropService {
  private dragStateSubject = new BehaviorSubject<DragState>({
    isDragging: false,
    dragType: 'node',
    dragData: null,
    startPosition: { x: 0, y: 0 }
  });

  public dragState$ = this.dragStateSubject.asObservable();

  constructor() {}

  startDrag(type: 'node' | 'canvas' | 'connection', data: any, startPosition: { x: number; y: number }): void {
    this.dragStateSubject.next({
      isDragging: true,
      dragType: type,
      dragData: data,
      startPosition
    });
  }

  updateDrag(currentPosition: { x: number; y: number }): void {
    const currentState = this.dragStateSubject.value;
    if (currentState.isDragging) {
      this.dragStateSubject.next({
        ...currentState,
        dragData: {
          ...currentState.dragData,
          currentPosition
        }
      });
    }
  }

  endDrag(): void {
    this.dragStateSubject.next({
      isDragging: false,
      dragType: 'node',
      dragData: null,
      startPosition: { x: 0, y: 0 }
    });
  }

  getCurrentDragState(): DragState {
    return this.dragStateSubject.value;
  }
}
