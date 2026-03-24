package hu.ridesharing.service;

import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.repository.RatingRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    private final String frontendUrl;

    private final RatingRepository ratingRepository;

    private final String from;

    private static final String ACCEPT_SUBJECT = "Passenger wants to join to your ride";

    private static final String LEFT_SUBJECT = "Passenger left your ride";

    private static final String CANCELED_SUBJECT = "Ride has been canceled";

    private static final String RATING_SUBJECT_FOR_PASSENGER = "You have to rate your driver(s)";

    private static final String RATING_SUBJECT_FOR_DRIVER = "You have to rate your passenger(s)";

    @Autowired
    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine,
                        RatingRepository ratingRepository, @Value("${app.frontend.url}") String frontendUrl,
                        @Value("${spring.mail.username}") String from) {

        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.ratingRepository = ratingRepository;
        this.frontendUrl = frontendUrl;
        this.from = from;
    }

    public void sendRideAcceptEmail(Journey journey, User passenger, String secureToken)
            throws MessagingException {

        User driver = journey.getDriver();
        double passengerRating = ratingRepository.findByPassengerRated(passenger, Pageable.unpaged()).stream()
                .mapToDouble(Rating::getValue)
                .average()
                .orElse(0);

        Context context = new Context();
        context.setVariable("passenger", passenger.getFullName());
        context.setVariable("passengerRating", passengerRating);
        context.setVariable("rideNumber", journey.getId());
        context.setVariable("fromCity", journey.getFromCity());
        context.setVariable("toCity", journey.getToCity());
        context.setVariable("driver", driver.getFullName());
        context.setVariable("depart", journey.getDepart().toString().replace("T", " "));
        context.setVariable("arrive", journey.getArrive().toString().replace("T", " "));
        context.setVariable("acceptLink", frontendUrl + "/drive/accept-passenger?token=" + secureToken);

        String to = driver.getEmailAddress();
        to = "gellerr.vrabely@gmail.com";

        this.sendEmailHelper("ride_accept_email", context, to, ACCEPT_SUBJECT);
    }

    public void sendPassengerLeftEmail(Journey journey, User passenger, int passengers) throws MessagingException {
        User driver = journey.getDriver();
        double passengerRating = ratingRepository.findByPassengerRated(passenger, Pageable.unpaged()).stream()
                .mapToDouble(Rating::getValue)
                .average()
                .orElse(0);

        Context context = new Context();
        context.setVariable("passenger", passenger.getFullName());
        context.setVariable("passengerRating", passengerRating);
        context.setVariable("rideNumber", journey.getId());
        context.setVariable("fromCity", journey.getFromCity());
        context.setVariable("toCity", journey.getToCity());
        context.setVariable("driver", driver.getFullName());
        context.setVariable("depart", journey.getDepart().toString().replace("T", " "));
        context.setVariable("passengers", passengers);

        String to = driver.getEmailAddress();
        to = "gellerr.vrabely@gmail.com";

        this.sendEmailHelper("passenger_left", context, to, LEFT_SUBJECT);
    }

    public void sendRideHasBeenCanceledEmail(Journey journey, User passenger) throws MessagingException {
        Context context = new Context();
        context.setVariable("passenger", passenger.getFullName());
        context.setVariable("rideNumber", journey.getId());
        context.setVariable("fromCity", journey.getFromCity());
        context.setVariable("toCity", journey.getToCity());
        context.setVariable("depart", journey.getDepart().toString().replace("T", " "));
        context.setVariable("rideId", journey.getId());

        String to = passenger.getEmailAddress();
        to = "gellerr.vrabely@gmail.com";

        this.sendEmailHelper("ride_has_been_canceled_email", context, to, CANCELED_SUBJECT);
    }

    public void sendRideHasChangedEmail(Journey journey, User passenger, String oldDepart) throws MessagingException {
        Context context = new Context();
        context.setVariable("passenger", passenger.getFullName());
        context.setVariable("rideNumber", journey.getId());
        context.setVariable("fromCity", journey.getFromCity());
        context.setVariable("toCity", journey.getToCity());
        context.setVariable("depart", oldDepart);
        context.setVariable("rideId", journey.getId());

        String to = passenger.getEmailAddress();
        to = "gellerr.vrabely@gmail.com";

        this.sendEmailHelper("ride_changed", context, to, "Ride has changed");
    }

    public void sendDriverRatingsToMakeEmail(User driver) throws MessagingException {
        Context context = new Context();
        context.setVariable("user", driver.getFullName());
        context.setVariable("frontendUrl", frontendUrl);
        context.setVariable("ratingUrl", frontendUrl + "/profile/drives");
        context.setVariable("role", "driver");

        String to = driver.getEmailAddress();
        to = "gellerr.vrabely@gmail.com";

        this.sendEmailHelper("rating", context, to, RATING_SUBJECT_FOR_DRIVER);
    }

    public void sendPassengerRatingsToMakeEmail(User passenger) throws MessagingException {
        Context context = new Context();
        context.setVariable("user", passenger.getFullName());
        context.setVariable("frontendUrl", frontendUrl);
        context.setVariable("ratingUrl", frontendUrl + "/profile/rides");
        context.setVariable("role", "passenger");

        String to = passenger.getEmailAddress();
        to = "gellerr.vrabely@gmail.com";

        this.sendEmailHelper("rating", context, to, RATING_SUBJECT_FOR_PASSENGER);
    }

    public void sendSimpleEmail(String to, String subject,String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject(subject);
        message.setTo(to);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendAdminEmail(Journey journey, Exception e, String adminEmail, String text, String subject) {
        sendSimpleEmail(adminEmail, subject, text.formatted(journey.getId(), e.getMessage()));
    }

    private void sendEmailHelper(String templateFile, Context context, String to, String subject)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String htmlContent = templateEngine.process(templateFile, context);
        helper.setTo(to);
        helper.setFrom("gellerr.vrabely@gmail.com");
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        log.debug("Sending email to {}", to);
        mailSender.send(message);
    }
}
