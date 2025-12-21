export class GeocodingReverseResponseModel {
  constructor(public name: string,
              public local_names: string[],
              public country: string,
              public state: string) {
  }
}
