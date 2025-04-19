import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BestratedComponent } from './bestrated.component';

describe('BestratedComponent', () => {
  let component: BestratedComponent;
  let fixture: ComponentFixture<BestratedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BestratedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BestratedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
