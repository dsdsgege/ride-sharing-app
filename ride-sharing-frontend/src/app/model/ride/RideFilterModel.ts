export class RideFilterModel {
  pickupFrom?: string | null;
  dropOffTo?: string | null;
  dateFrom?: Date | null;
  dateTo?: Date | null;
  seats?: number | null;
  maxPrice?: number | null;
  rating?: number | null;
  showWithoutRating!: boolean | null;
  page!: number;
  pageSize!: number;
  sortBy!: string;
  direction!: string;
}
