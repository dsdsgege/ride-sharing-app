import { Injectable } from '@angular/core';
import {FormControl} from '@angular/forms';

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

  public setLocalStorageOnValueChanges(key: string, control: FormControl) {
    control.valueChanges.subscribe( value => localStorage.setItem(key, value ?? ''));
  }
}
