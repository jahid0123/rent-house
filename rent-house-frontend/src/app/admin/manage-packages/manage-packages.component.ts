import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BuyPackageService } from '../../page/buy-package/service/buy-package.service';
import { ManagePackagesService } from './service/manage-packages.service';
import { GetAllCreditPackage, AddCreditPackage } from '../../model/class';

@Component({
  selector: 'app-manage-packages',
  imports: [FormsModule, CommonModule],
  templateUrl: './manage-packages.component.html',
  styleUrls: ['./manage-packages.component.css'],
})
export class ManagePackagesComponent implements OnInit {

  allCreditPackage: GetAllCreditPackage[] = [];
  allCreditPackageOriginal: GetAllCreditPackage[] = [];
  filterText: string = '';
  loadError: string | null = null;

  selectedPackage: AddCreditPackage = {
    id: 0,
    name: '',
    creditAmount: 0,
    price: 0,
  };
  showModal: boolean = false;
  isEditMode: boolean = false;

  constructor(
    private buyPackageService: BuyPackageService,
    private router: Router,
    private manageService: ManagePackagesService
  ) {}

  ngOnInit(): void {
    this.loadAllPackages();
  }

  loadAllPackages(): void {
    console.log('ManagePackagesComponent: calling getAllPackages()');
    this.loadError = null;
    this.buyPackageService.getAllPackages().subscribe({
      next: (res: GetAllCreditPackage[]) => {
        this.allCreditPackage = res || [];
        // keep an original copy for filtering
        this.allCreditPackageOriginal = [...this.allCreditPackage];
      },
      error: (err) => {
        console.error('Failed to load packages:', err);
        this.loadError = 'Failed to load packages. Check console for details.';
        this.allCreditPackage = [];
        this.allCreditPackageOriginal = [];
      },
    });
  }

  applyFilter() {
    const text = (this.filterText || '').trim().toLowerCase();
    if (!text) {
      this.allCreditPackage = [...this.allCreditPackageOriginal];
      return;
    }
    this.allCreditPackage = this.allCreditPackageOriginal.filter(p => {
      return (
        (p.name || '').toString().toLowerCase().includes(text) ||
        (p.id || '').toString().toLowerCase().includes(text) ||
        (p.creditAmount || '').toString().toLowerCase().includes(text) ||
        (p.price || '').toString().toLowerCase().includes(text)
      );
    });
  }

  // Open modal to add package
  openAddModal() {
    this.isEditMode = false;
    this.selectedPackage = { id: 0, name: '', creditAmount: 0, price: 0 };
    this.showModal = true;
  }

  // Open modal to edit package
  openEditModal(pkg: any) {
    this.isEditMode = true;
    this.selectedPackage = { ...pkg };
    this.showModal = true;
  }

  // Close modal
  closeModal(): void {
    this.showModal = false;
  }

  savePackage() {
    // placeholder: keep existing behavior if manageService is implemented
    if (this.isEditMode) {
      // update locally
      const idx = this.allCreditPackage.findIndex(p => p.id === this.selectedPackage.id);
      if (idx !== -1) this.allCreditPackage[idx] = { ...this.selectedPackage };
    } else {
      const newId = Date.now();
      this.allCreditPackage.unshift({ ...this.selectedPackage, id: newId });
    }
    // refresh original copy
    this.allCreditPackageOriginal = [...this.allCreditPackage];
    this.closeModal();
  }

  // Delete package
  deletePackage(id: number): void {
    const confirmed = confirm('Are you sure you want to delete this package?');
    if (!confirmed) return;
    // optimistic UI remove, real deletion should call backend via manageService
    this.allCreditPackage = this.allCreditPackage.filter(p => p.id !== id);
    this.allCreditPackageOriginal = [...this.allCreditPackage];
  }
}
