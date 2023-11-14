import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifyEventsComponent } from './modify-events.component';

describe('ModifyEventsComponent', () => {
  let component: ModifyEventsComponent;
  let fixture: ComponentFixture<ModifyEventsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModifyEventsComponent]
    });
    fixture = TestBed.createComponent(ModifyEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
