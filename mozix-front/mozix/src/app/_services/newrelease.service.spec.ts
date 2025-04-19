import { TestBed } from '@angular/core/testing';

import { NewreleaseService } from './newrelease.service';

describe('NewreleaseService', () => {
  let service: NewreleaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NewreleaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
