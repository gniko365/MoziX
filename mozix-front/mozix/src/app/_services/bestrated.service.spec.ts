import { TestBed } from '@angular/core/testing';

import { BestratedService } from './bestrated.service';

describe('BestratedService', () => {
  let service: BestratedService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BestratedService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
