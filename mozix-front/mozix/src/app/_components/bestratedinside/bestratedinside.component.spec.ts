import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BestratedinsideComponent } from './bestratedinside.component';

describe('BestratedinsideComponent', () => {
  let component: BestratedinsideComponent;
  let fixture: ComponentFixture<BestratedinsideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BestratedinsideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BestratedinsideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
