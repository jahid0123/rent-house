import { Component, OnInit } from '@angular/core';
import { MyPurchasePackageHistory } from '../../model/class';
import { PurchaseHistoryService } from './service/purchase-history.service';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-purchase-history',
  imports: [NgFor, NgIf],
  templateUrl: './purchase-history.component.html'
})
export class PurchaseHistoryComponent implements OnInit {
  creditPackages: MyPurchasePackageHistory[] = [];

  constructor(private historyService: PurchaseHistoryService) {}

  ngOnInit(): void {
    // Replace this with actual API call
    this.loadUserPurchaseHistory()
  }


  loadUserPurchaseHistory(): void {
      const userId = localStorage.getItem('id');
      if (!userId) return;
  
      this.historyService.getPurchaseHistory().subscribe({
        next: (res: MyPurchasePackageHistory[]) => {
          this.creditPackages = res;
        },
        error: (err) => {
          console.error('Failed to load purchase history info!!', err);
        }
      });
    }

}