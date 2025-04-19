import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryinsideComponent } from './categoryinside.component';

describe('CategoryinsideComponent', () => {
  let component: CategoryinsideComponent;
  let fixture: ComponentFixture<CategoryinsideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryinsideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CategoryinsideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
