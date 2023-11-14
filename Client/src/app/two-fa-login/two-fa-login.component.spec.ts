import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TwoFALoginComponent } from './two-fa-login.component';

describe('TwoFALoginComponent', () => {
  let component: TwoFALoginComponent;
  let fixture: ComponentFixture<TwoFALoginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TwoFALoginComponent]
    });
    fixture = TestBed.createComponent(TwoFALoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
