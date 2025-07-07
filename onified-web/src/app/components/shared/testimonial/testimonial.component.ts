import { Component, Input, OnInit } from '@angular/core';

export interface Testimonial {
  quote: string;
  author: string;
  title: string;
  avatar?: string;
}

@Component({
  selector: 'app-testimonial',
  standalone: false,
  templateUrl: './testimonial.component.html',
  styleUrls: ['./testimonial.component.scss']
})
export class TestimonialComponent implements OnInit {
  @Input() testimonial: Testimonial = {
    quote: "Onified.ai transformed our workflow completely. The authentication system is seamless and our team loves how easy it is to use.",
    author: "Sarah Johnson",
    title: "CTO"
  };

  ngOnInit() {
  }

  // Getter methods to ensure we always return strings
  get testimonialQuote(): string {
    return this.testimonial?.quote || '';
  }

  get testimonialAuthor(): string {
    return this.testimonial?.author || '';
  }

  get testimonialTitle(): string {
    return this.testimonial?.title || '';
  }
}