import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewreleaseComponent } from './newrelease.component';

describe('NewreleaseComponent', () => {
  let component: NewreleaseComponent;
  let fixture: ComponentFixture<NewreleaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewreleaseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewreleaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
