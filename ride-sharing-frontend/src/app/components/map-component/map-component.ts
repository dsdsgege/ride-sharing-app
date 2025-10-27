import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map-component',
  imports: [],
  templateUrl: './map-component.html',
  styleUrl: './map-component.scss'
})
export class MapComponent implements OnChanges {

  @Input("position")
  position: GeolocationPosition | null = null;
  map!: L.Map;
  zoom: number = 16;

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['position'] && this.position) {
      if(!this.map) {
        this.initMap();
        return;
      } else {
        this.map.setView([this.position.coords.latitude, this.position.coords.longitude], this.zoom);
      }
    }
  }

  private initMap(): void {
    if(!this.position) {
      return;
    }
    this.map = L.map('map', {
      center: [this.position.coords.latitude, this.position.coords.longitude],
      zoom: this.zoom,
    })

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    L.marker([this.position.coords.latitude, this.position.coords.longitude]).addTo(this.map);
  }
}
