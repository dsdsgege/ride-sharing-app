package hu.ridesharing.dto.request;
import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;
import lombok.Data;

@Data
public class AddDriveRequest {
    private Journey drive;
    private Driver driver;
}