import { Injectable, Type } from '@angular/core';
import { loadRemoteModule } from '@angular-architects/module-federation';
import { Observable, from } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PortalLoaderService {
  loadPortalComponent(remoteName: string): Observable<Type<any>> {
    console.log('Loading remote module', remoteName);
    return from(
      loadRemoteModule({
        remoteEntry: this.getRemoteEntry(remoteName),
        type: 'module',
        exposedModule: './Component',
      }).then(m => m.App)
    );
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