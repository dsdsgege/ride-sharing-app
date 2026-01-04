import { Injectable } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FormService {

  /**
   * Setting the FormControl from localStorage without valueChanges
   *
   * @param key
   * @param control
   * @private
   */
  public setValueFromLocalstorage(key: string, control: FormControl): void {
    const storedValue = localStorage.getItem(key);
    if(storedValue) {
      control.setValue(storedValue, { emitEvent: false });
    }
  }

  public setValueFromLocastorageForDate(key: string, control: FormControl<Date | null>) {
    const storedDateString = localStorage.getItem(key);
    if (storedDateString) {
      const date = new Date(storedDateString);
      if (!Number.isNaN(date.getTime())) {
        control.setValue(date);
      }
    }
  }

  public setLocalStorageOnValueChanges(key: string, control: FormControl) {
    control.valueChanges.subscribe( value => localStorage.setItem(key, value ?? ''));
  }

  public setLocalStorageForDateOnValueChanges(key: string, control: FormControl<Date | null>) {
    control.valueChanges.subscribe(value =>
      localStorage.setItem(key, JSON.stringify(value ?? '')));
  }

  public persistForm(form: FormGroup, key: string): void {
    form.valueChanges.subscribe(value => {
      localStorage.setItem(key, JSON.stringify(value));
    });
  }

  public loadForm(form: FormGroup, key: string): void {
    const storedValue = localStorage.getItem(key);
    if (storedValue) {
      try {
        const parsedValue = JSON.parse(storedValue);

        if (parsedValue.depart) parsedValue.depart = new Date(parsedValue.depart);
        if (parsedValue.arrive) parsedValue.arrive = new Date(parsedValue.arrive);

        form.patchValue(parsedValue, { emitEvent: false });
      } catch (e) {
        console.error("Error loading local storage", e);
      }
    }
  }

  /**
   * Checks if all form controls are filled.
   *
   * @param forms FormControl array
   */
  public areInputsFilled(...forms: FormControl[]): boolean {
    return forms.every(form => {
      const formValue = form.value;

      if (typeof formValue === 'string') {
        return formValue.trim() !== '';
      }

      if (formValue instanceof Date) {
        console.log(formValue);
        return formValue > new Date();
      }

      if (typeof formValue === 'number') {
        return formValue > 0;
      }

      return false;
    });
  }
}
