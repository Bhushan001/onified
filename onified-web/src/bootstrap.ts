import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

// Ensure Module Federation is properly initialized
declare const require: any;

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch((err: unknown) => console.error(err)); 