import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { MainHeaderComponent } from "./section/header/main-header/main-header.component";
import { FooterComponent } from "./section/footer/footer.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FooterComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  
  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.removeModalArtifacts();
      }
    });
  }

  removeModalArtifacts(): void {
    const backdrops = document.getElementsByClassName('modal-backdrop');
    while (backdrops.length > 0) {
      backdrops[0].remove();
    }

    document.body.classList.remove('modal-open');
  }
}
