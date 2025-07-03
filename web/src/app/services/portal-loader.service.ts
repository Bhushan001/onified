import { Injectable, Type } from '@angular/core';
import { loadRemoteModule } from '@angular-architects/module-federation';
import { Observable, from } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PortalLoaderService {
  private loadedStyles = new Set<string>();

  loadPortalComponent(remoteName: string): Observable<Type<any>> {
    console.log('Loading remote module', remoteName);
    return from(
      loadRemoteModule({
        remoteEntry: this.getRemoteEntry(remoteName),
        type: 'module',
        exposedModule: remoteName === 'hub' ? './Dashboard' : './Component',
      }).then(async (m) => {
        // Load styles for the remote module
        await this.loadRemoteStyles(remoteName);
        return remoteName === 'hub' ? m.DashboardWrapperComponent : m.HubComponent;
      })
    );
  }

  private async loadRemoteStyles(remoteName: string): Promise<void> {
    if (this.loadedStyles.has(remoteName)) {
      return; // Styles already loaded
    }

    try {
      if (remoteName === 'hub') {
        // Load hub styles
        await loadRemoteModule({
          remoteEntry: this.getRemoteEntry(remoteName),
          type: 'module',
          exposedModule: './Styles',
        });
        this.loadedStyles.add(remoteName);
      }
      // Add other remotes as needed
    } catch (error) {
      console.warn(`Failed to load styles for ${remoteName}:`, error);
    }
  }

  private getRemoteEntry(remoteName: string): string {
    switch (remoteName) {
      case 'hub': return 'http://localhost:4300/remoteEntry.js';
      case 'console': return 'http://localhost:4400/remoteEntry.js';
      case 'workspace': return 'http://localhost:4500/remoteEntry.js';
      default: throw new Error('Unknown remote');
    }
  }
} 