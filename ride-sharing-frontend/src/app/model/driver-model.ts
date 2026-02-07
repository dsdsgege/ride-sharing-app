export class DriverModel {
  constructor(public username: string,
              public fullName: string,
              private emailAddress: string,
              public rating: number) {
  }
}
