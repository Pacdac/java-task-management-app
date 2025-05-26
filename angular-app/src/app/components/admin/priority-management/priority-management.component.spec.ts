import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PriorityManagementComponent } from './priority-management.component';

describe('PriorityManagementComponent', () => {
  let component: PriorityManagementComponent;
  let fixture: ComponentFixture<PriorityManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriorityManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PriorityManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
