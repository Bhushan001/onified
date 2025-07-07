import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-remote-unavailable',
  templateUrl: './remote-unavailable.component.html',
  styleUrls: ['./remote-unavailable.component.scss']
})
export class RemoteUnavailableComponent {
  constructor(private router: Router) {}

  goBack(): void {
    this.router.navigate(['/login']);
  }
} 