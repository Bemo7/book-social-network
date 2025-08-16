import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-books',
  imports: [RouterOutlet],
  templateUrl: './books.html',
  styleUrl: './books.scss',
  standalone: true
})
export class Books {

}
