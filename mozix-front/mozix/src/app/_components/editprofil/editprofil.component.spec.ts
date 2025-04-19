import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditprofilComponent } from './editprofil.component';

describe('EditprofilComponent', () => {
  let component: EditprofilComponent;
  let fixture: ComponentFixture<EditprofilComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditprofilComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditprofilComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
