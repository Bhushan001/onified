import { Component, ViewChild, ViewContainerRef, AfterViewInit, Injector, OnDestroy } from '@angular/core';
import { PortalLoaderService } from '../../services/portal-loader.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-portal-host',
  template: `<div [class.hub-styles]="isHubApp"><ng-container #vc></ng-container></div>`,
  standalone: true,
  imports: []
})
export class PortalHostComponent implements AfterViewInit, OnDestroy {
  @ViewChild('vc', { read: ViewContainerRef, static: true }) vc!: ViewContainerRef;
  remoteName!: string;
  isHubApp = false;
  private sub?: Subscription;

  constructor(
    private portalLoader: PortalLoaderService,
    private injector: Injector,
    private route: ActivatedRoute
  ) {}

  ngAfterViewInit() {
    this.remoteName = this.route.snapshot.paramMap.get('remote')!;
    console.log(this.remoteName);
    
    // Set flag for hub app to apply specific styles
    this.isHubApp = this.remoteName === 'hub';
    
    if (this.remoteName) {
      this.sub = this.portalLoader.loadPortalComponent(this.remoteName).subscribe(component => {
        console.log('Creating component', component);
        this.vc.createComponent(component, { injector: this.injector });
      });
    }
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }
} 