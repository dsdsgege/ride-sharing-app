package hu.ridesharing.dto.request;
import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;

public record AddDriveRequest(Journey drive, Driver driver) {
}