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

    private static final String SUBJECT = "Passenger wants to join to your ride";

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
        double passengerRating = ratingRepository.findByPassenger(passenger).stream()
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

        MimeMessage message = mailSender.createMimeMessage();

        String htmlContent = templateEngine.process("ride_accept_email", context);
        String to = driver.getEmailAddress();
        //TODO DELETE:
        to = "gellerr.vrabely@gmail.com";

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom("gellerr.vrabely@gmail.com");
        helper.setSubject(SUBJECT);
        helper.setText(htmlContent, true);

        log.debug("Sending email to {}", to);
        mailSender.send(message);
    }
}
