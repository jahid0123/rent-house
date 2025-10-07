import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RentPost } from '../../model/class';
import { ManagePostService } from './service/manage-post.service';
import { FormsModule } from '@angular/forms';
import { Modal } from 'bootstrap';



@Component({
  selector: 'app-manage-post',
  imports: [NgIf, CommonModule, NgFor, FormsModule],
  templateUrl: './manage-post.component.html',
  styleUrl: './manage-post.component.css',
})
export class ManagePostComponent implements OnInit {
  rentPosts = signal<any[]>([]);
  selectedPost!: RentPost;

  constructor(
    private postService: ManagePostService,

  ) {}

  ngOnInit(): void {
    this.cleanupModalArtifacts();
    this.loadPostInfo();
  }

  loadPostInfo(): void {
    const userId = localStorage.getItem('id');
    if (!userId) return;

    this.postService.getAllPost().subscribe({
      next: (res: RentPost[]) => {
          this.rentPosts.update(()=> res);
      },
      error: (err) => {
        console.error('Failed to load post info:', err);
      },
    });
  }

  openStatusDialog(post: RentPost): void {
    this.selectedPost = { ...post };
    const modalEl = document.getElementById('statusModal');
    if (modalEl) {
      const modal = new Modal(modalEl);
      modal.show();
    }
  }

updateStatus(): void {
  const index = this.rentPosts().findIndex(
    (p) => p.postId === this.selectedPost.postId
  );

  if (index !== -1) {
    // Update signal properly
    this.rentPosts.update(posts => {
      posts[index] = {
        ...posts[index],
        isAvailable: this.selectedPost.isAvailable
      };
      return [...posts]; // must return a new array to trigger reactivity
    });

    const data = {
      postId: this.selectedPost.postId,
      isAvailable: this.selectedPost.isAvailable,
    };

    this.postService.editPost(data).subscribe({
      next: () => {
        alert('Successfully updated.');
        this.closeModal('statusModal');
      },
      error: () => {
        alert('Update process failed!');
      },
    });
  }
}


  deletePost(postId: number): void {
    const confirmed = confirm('Are you sure you want to delete this post?');
    if (confirmed) {
      this.postService.deletePost(postId).subscribe({
        next: () => {
          this.rentPosts.update(()=> this.rentPosts().filter(
            (post) => post.postId !== postId
          ));
          alert('Deleted successfully.');
        },
        error: () => {
          alert('Delete process failed!');
        },
      });
    }
  }

  closeModal(modalId: string): void {
    const modalEl = document.getElementById(modalId);
    if (modalEl) {
      const modal = Modal.getInstance(modalEl);
      modal?.hide();

      modalEl.classList.remove('show');
      document.body.classList.remove('modal-open');

      this.removeBackdrop();
    }
  }

  removeBackdrop(): void {
    const backdrops = document.getElementsByClassName('modal-backdrop');
    while (backdrops.length > 0) {
      backdrops[0].remove();
    }
  }

  cleanupModalArtifacts(): void {
    document.body.classList.remove('modal-open');
    this.removeBackdrop();
  }
}