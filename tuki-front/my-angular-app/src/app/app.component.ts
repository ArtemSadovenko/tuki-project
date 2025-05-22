import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ImageSearchComponent } from './image-search/image-search.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ImageSearchComponent],
  template: `<app-image-search></app-image-search>`
})
export class AppComponent {}
