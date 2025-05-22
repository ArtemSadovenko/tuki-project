import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpEventType } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface ImageDTO {
  id: number;
  fileName: string;
  contentType: string;
  base64Data: string;
  tags: string[];
}

interface UploadItem {
  fileName: string;
  status: 'uploading' | 'success' | 'error';
  progress: number;
  file?: File;
}


@Component({
  selector: 'app-image-search',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './image-search.component.html',
  styleUrls: ['./image-search.component.css']
})
export class ImageSearchComponent {
  searchValue = '';
  images: ImageDTO[] = [];
  isLoading = false;
  isDragOver = false;
  uploadingFiles: UploadItem[] = [];

  constructor(private http: HttpClient) {}

  onSearch(): void {
    const tags = this.searchValue
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0);

    if (tags.length === 0) {
      return;
    }

    this.isLoading = true;
    this.images = [];

    this.http.post<ImageDTO[]>('http://localhost:8500/api/v1/image/search', tags)
      .subscribe({
        next: (res) => {
          this.images = res;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error:', err);
          this.isLoading = false;
        }
      });
  }

  onImageError(event: any): void {
    event.target.src = '/api/placeholder/280/220';
  }



  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.handleFiles(Array.from(files));
    }
    // Reset the input
    event.target.value = '';
  }

  private handleFiles(files: File[]): void {
    const imageFiles = files.filter(file => file.type.startsWith('image/'));
    const maxSize = 10 * 1024 * 1024; // 10MB

    imageFiles.forEach(file => {
      if (file.size > maxSize) {
        console.error(`File ${file.name} is too large. Maximum size is 10MB.`);
        return;
      }

      const uploadItem: UploadItem = {
        fileName: file.name,
        status: 'uploading',
        progress: 0,
        file: file
      };

      this.uploadingFiles.push(uploadItem);
      this.uploadFile(uploadItem);
    });
  }

  private uploadFile(uploadItem: UploadItem): void {
    if (!uploadItem.file) return;

    const formData = new FormData();
    formData.append('file', uploadItem.file);

    this.http.post('http://localhost:8500/api/v1/image', formData, {
      reportProgress: true,
      observe: 'events'
    }).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress) {
          if (event.total) {
            uploadItem.progress = Math.round(100 * event.loaded / event.total);
          }
        } else if (event.type === HttpEventType.Response) {
          uploadItem.status = 'success';
          uploadItem.progress = 100;
          
          setTimeout(() => {
            const index = this.uploadingFiles.indexOf(uploadItem);
            if (index > -1) {
              this.uploadingFiles.splice(index, 1);
            }
          }, 3000);
        }
      },
      error: (error) => {
        console.error('Upload error:', error);
        uploadItem.status = 'error';
        
        setTimeout(() => {
          const index = this.uploadingFiles.indexOf(uploadItem);
          if (index > -1) {
            this.uploadingFiles.splice(index, 1);
          }
        }, 5000);
      }
    });
  }
}