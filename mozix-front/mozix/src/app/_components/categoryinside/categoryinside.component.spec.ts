import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryInsideComponent } from './categoryinside.component';

describe('CategoryinsideComponent', () => {
  let component: CategoryInsideComponent;
  let fixture: ComponentFixture<CategoryInsideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryInsideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CategoryInsideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
