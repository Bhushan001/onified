import { Injectable, Type } from '@angular/core';
import { loadRemoteModule } from '@angular-architects/module-federation';
import { Observable, from } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PortalLoaderService {
  private loadedStyles = new Set<string>();

  loadPortalComponent(remoteName: string): Observable<Type<any>> {
    console.log('Loading remote module', remoteName);
    if (remoteName === 'hub') {
      return from(
        loadRemoteModule({
          remoteEntry: this.getRemoteEntry(remoteName),
          type: 'module',
          exposedModule: './Dashboard',
        }).then(async (m) => {
          // Optionally load styles if you expose them in the future
          // await this.loadRemoteStyles(remoteName);
          return m.DashboardWrapperComponent;
        })
      );
    } else if (remoteName === 'console') {
      return from(
        loadRemoteModule({
          remoteEntry: this.getRemoteEntry(remoteName),
          type: 'module',
          exposedModule: './Dashboard',
        }).then(async (m) => {
          // Optionally load styles if you expose them in the future
          // await this.loadRemoteStyles(remoteName);
          return m.DashboardWrapperComponent;
        })
      );
    } else if (remoteName === 'workspace') {
      return from(
        loadRemoteModule({
          remoteEntry: this.getRemoteEntry(remoteName),
          type: 'module',
          exposedModule: './Dashboard',
        }).then(async (m) => {
          // Optionally load styles if you expose them in the future
          // await this.loadRemoteStyles(remoteName);
          return m.DashboardWrapperComponent;
        })
      );
    }
    // Add logic for other remotes as needed
    throw new Error('Unknown remote or not implemented');
  }

  // Optionally keep this for future style loading
  // private async loadRemoteStyles(remoteName: string): Promise<void> {
  //   if (this.loadedStyles.has(remoteName)) {
  //     return; // Styles already loaded
  //   }
  //   try {
  //     if (remoteName === 'hub') {
  //       await loadRemoteModule({
  //         remoteEntry: this.getRemoteEntry(remoteName),
  //         type: 'module',
  //         exposedModule: './Styles',
  //       });
  //       this.loadedStyles.add(remoteName);
  //     }
  //   } catch (error) {
  //     console.warn(`Failed to load styles for ${remoteName}:`, error);
  //   }
  // }

  private getRemoteEntry(remoteName: string): string {
    switch (remoteName) {
      case 'hub': return 'http://localhost:4300/remoteEntry.js';
      case 'console': return 'http://localhost:4400/remoteEntry.js';
      case 'workspace': return 'http://localhost:4500/remoteEntry.js';
      // Add other remotes as needed
      default: throw new Error('Unknown remote');
    }
  }
} 