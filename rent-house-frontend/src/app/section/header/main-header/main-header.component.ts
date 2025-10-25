import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';

import { AuthService } from '../../../core/service/auth/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-main-header',
  imports: [RouterModule],
  templateUrl: './main-header.component.html'
})
export class MainHeaderComponent implements OnInit {

  user = {
    name: '',
    avatarUrl: 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'
  };

  role: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.role$.subscribe(role => {
      this.role = role;
    });

     const name = localStorage.getItem('username');
    const avatar = localStorage.getItem('avatarUrl');

    if (name) this.user.name = name;
    if (avatar) this.user.avatarUrl = avatar;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
