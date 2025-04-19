import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToldiComponent } from './toldi.component';

describe('ToldiComponent', () => {
  let component: ToldiComponent;
  let fixture: ComponentFixture<ToldiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToldiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToldiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
