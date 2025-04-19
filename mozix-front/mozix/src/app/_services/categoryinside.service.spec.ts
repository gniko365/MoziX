import { TestBed } from '@angular/core/testing';

import { CategoryinsideService } from './categoryinside.service';

describe('CategoryinsideService', () => {
  let service: CategoryinsideService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoryinsideService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
