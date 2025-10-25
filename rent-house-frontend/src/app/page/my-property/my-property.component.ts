import { CommonModule, NgClass, NgFor } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MyPropertyService } from './service/my-property.service';
import { MyPostedProperty } from '../../model/class';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-property',
  imports: [NgFor, NgClass, ReactiveFormsModule, CommonModule],
  templateUrl: './my-property.component.html',
})
export class MyPropertyComponent implements OnInit {
  flatList: MyPostedProperty[] = [];
  editForm: FormGroup;
  showModal: boolean = false;
  selectedFlatId: number | null = null;

  categories = [
    'FAMILY',
    'BACHELOR',
    'SUBLET',
    'ROOMMATE',
    'SHOP', 
    'OFFICE',
    'HOUSE',
  ];
  divisions = ['Dhaka', 'Chattogram', 'Khulna', 'Barisal'];
  districts = ['Dhaka', 'Gazipur', 'Narayanganj'];
  thanas = ['Dhanmondi', 'Mirpur', 'Uttara'];
  constructor(
    private myPropertyService: MyPropertyService,
    private router: Router,
    private fb: FormBuilder,
  ) {
    this.editForm = this.fb.group({
      postId: [''],
      contactNumber: [''],
      contactPerson: [''],
      availableFrom: [''],
      category: [''],
      title: [''],
      description: [''],
      rentAmount: [0],
      division: [''],
      district: [''],
      thana: [''],
      section: [''],
      roadNumber: [''],
      houseNumber: [''],
      address: [''],
    });
  }

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    this.myPropertyService.getMyPostedProperty().subscribe({
      next: (res: MyPostedProperty[]) => {
        this.flatList = res;
      },
      error: () => {
        console.error('Failed to load user info');
      },
    });
  }

  onEdit(flat: MyPostedProperty): void {
    this.selectedFlatId = flat.id;
    this.editForm.patchValue({
      ...flat,
      postId: flat.id,
      availableFrom: flat.availableFrom?.toString().split('T')[0],
    });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editForm.reset();
  }

  submitEdit(): void {
    if (this.editForm.valid) {
      const updatedFlat = this.editForm.value;
      this.myPropertyService.updateMyPostedProperty(updatedFlat).subscribe({
        next: () => {
          alert('Information updated successfully.');
          this.closeModal();
          this.loadUserInfo();
          this.router.navigateByUrl('/user-dashboard/my-property');
        },
        error: () => {
          alert('Data not update!!!');
        },
      });
    }
  }

  onDelete(id: number): void {
    if (confirm('Are you sure to delete this flat?')) {
      this.myPropertyService.deleteMyPostedProperty(id).subscribe({
        next: () => {
          alert('Post delete successfully.');
          this.loadUserInfo();
        },
        error: () => {
          alert('Something wrong!');
        },
      });
    }
  }
}
