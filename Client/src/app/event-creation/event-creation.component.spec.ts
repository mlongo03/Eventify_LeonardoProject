import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventCreationComponent } from './event-creation.component';

describe('EventCreationComponent', () => {
  let component: EventCreationComponent;
  let fixture: ComponentFixture<EventCreationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EventCreationComponent]
    });
    fixture = TestBed.createComponent(EventCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
