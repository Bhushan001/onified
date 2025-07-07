import { Component, ViewChild, ViewContainerRef, AfterViewInit, Injector, OnDestroy, OnInit } from '@angular/core';
import { PortalLoaderService } from '../../services/portal-loader.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-portal-host',
  standalone: false,
  template: `<div [class.hub-styles]="isHubApp"><ng-container #vc></ng-container></div>`,
  styles: [`
    .hub-styles {
      /* Add any hub-specific styles here */
    }
  `]
})
export class PortalHostComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('vc', { read: ViewContainerRef, static: true }) vc!: ViewContainerRef;
  remoteName!: string;
  isHubApp = false;
  private sub?: Subscription;

  constructor(
    private portalLoader: PortalLoaderService,
    private injector: Injector,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.remoteName = this.route.snapshot.paramMap.get('remote')!;
    this.isHubApp = this.remoteName === 'hub';
  }

  ngAfterViewInit() {
    if (this.remoteName) {
      this.sub = this.portalLoader.loadPortalComponent(this.remoteName).subscribe(component => {
        this.vc.createComponent(component, { injector: this.injector });
      });
    }
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }
} 