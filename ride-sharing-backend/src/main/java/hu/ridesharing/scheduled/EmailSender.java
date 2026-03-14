package hu.ridesharing.scheduled;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import hu.ridesharing.entity.User;
import hu.ridesharing.service.EmailService;
import hu.ridesharing.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class EmailSender {

    private final RatingService ratingService;

    private final EmailService emailService;

    private final RetryPolicy<Void> retryPolicy;

    public EmailSender(EmailService emailService, RatingService ratingService) {
        this.emailService = emailService;
        this.ratingService = ratingService;
        this.retryPolicy = RetryPolicy.<Void>builder()
                .handle(HttpClientErrorException.TooManyRequests.class)
                .withDelay(Duration.ofMinutes(10))
                .withMaxRetries(5)
                .build();
    }

    /**
     * This method sends an automated email about ratings to make. Every Tuesday at 8pm as this time gets the most
     * activity. Refer to
     * <a href="https://customer.io/learn/lifecycle-marketing/email-sending-schedule">
     *     this study.
     * </a>
     */
    @Scheduled(cron = "0 0 20 * * 2")
    public void sendRatingEmail() {
        List<User> eligibleDrivers = ratingService.getDriversEligibleForRatingEmail();
        for (User driver : eligibleDrivers) {
            try {
                Failsafe.with(retryPolicy).run(() -> emailService.sendDriverRatingsToMakeEmail(driver));
            } catch (Exception e) {
                log.error("Failed to send rating email to driver {} after max retries.", driver.getUsername(), e);
            }
        }

        List<User> eligiblePassengers = ratingService.getPassengersEligibleForRatingEmail();
        for (User passenger : eligiblePassengers) {
            try {
                Failsafe.with(retryPolicy).run(() -> emailService.sendPassengerRatingsToMakeEmail(passenger));
            } catch (Exception e) {
                log.error("Failed to send rating email to passenger {} after max retries.", passenger.getUsername(), e);
            }
        }
    }
}
