import { Component, OnInit } from '@angular/core';
import { UnlockedPost } from '../../model/class';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UnlockPropertyService } from './service/unlock-property.service';

@Component({
  selector: 'app-unlock-property',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './unlock-property.component.html',
})
export class UnlockPropertyComponent implements OnInit {

  unlockedPosts: UnlockedPost[] = [];
  selectedPost: UnlockedPost | null = null;
  showModal = false;

  constructor(private unlockService: UnlockPropertyService) {}

  ngOnInit(): void {
    this.loadUnlockPropertyList();
  }

  loadUnlockPropertyList(): void {
    this.unlockService.getMyUnlockProperty().subscribe({
      next: (res: UnlockedPost[]) => {
        this.unlockedPosts = res;
      },
      error: () => {
        console.error('Failed to fetch unlock property info');
      },
    });
  }

  viewDetails(post: UnlockedPost): void {
    this.selectedPost = post;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedPost = null;
  }
}