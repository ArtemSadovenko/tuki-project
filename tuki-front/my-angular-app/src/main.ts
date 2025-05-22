import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { ImageSearchComponent } from './app/image-search/image-search.component';

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
