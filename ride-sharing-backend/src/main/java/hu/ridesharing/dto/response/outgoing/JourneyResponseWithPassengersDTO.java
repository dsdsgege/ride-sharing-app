package hu.ridesharing.dto.response.outgoing;

import hu.ridesharing.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class JourneyResponseWithPassengersDTO extends JourneyResponseDTO {
    private List<User> passengersToRate;
}
