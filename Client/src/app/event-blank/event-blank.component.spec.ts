import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventBlankComponent } from './event-blank.component';

describe('EventBlankComponent', () => {
  let component: EventBlankComponent;
  let fixture: ComponentFixture<EventBlankComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EventBlankComponent]
    });
    fixture = TestBed.createComponent(EventBlankComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
