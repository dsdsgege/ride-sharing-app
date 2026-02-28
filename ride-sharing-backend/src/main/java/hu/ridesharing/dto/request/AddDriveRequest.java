package hu.ridesharing.dto.request;
import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Journey;

public record AddDriveRequest(Journey drive, User driver) {
}