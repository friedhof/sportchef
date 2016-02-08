package ch.sportchef.business.authentication.boundary;

import ch.sportchef.business.authentication.entity.SimpleTokenCredential;
import ch.sportchef.business.configuration.boundary.ConfigurationManager;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.boundary.UserService;
import ch.sportchef.business.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private static final String CHALLENGE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CHALLENGE_LENGTH = 10;
    private static final TemporalAmount TOKEN_EXPIRATION_TIME = Duration.ofDays(1);

    private final Key jwtSigningKey = MacProvider.generateKey();

    @Inject
    private UserService userService;

    @Inject
    private ConfigurationManager configurationManager;
    private Configuration configuration;

    private Cache<String, String> challengeCache;

    @PostConstruct
    private void init() {
        configuration = configurationManager.getConfiguration();
        challengeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public boolean requestChallenge(@NotNull final String email) throws EmailException {
        final Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            final String challenge = generateChallenge();
            challengeCache.put(email, challenge);
            sendChallenge(email, challenge);
            return true;
        }
        return false;
    }

    private String generateChallenge() {
        final StringBuilder challenge = new StringBuilder();
        final Random random = new Random();
        final int bound = CHALLENGE_CHARACTERS.length();
        int challengeCount = 0;
        while (challengeCount < CHALLENGE_LENGTH) {
            if (challengeCount > 0 && challengeCount % 5 == 0) {
                challenge.append('-');
            }
            final int index = random.nextInt(bound);
            challenge.append(CHALLENGE_CHARACTERS.charAt(index));
            challengeCount++;
        }
        return challenge.toString();
    }

    private void sendChallenge(@NotNull final String email, @NotNull final String challenge) throws EmailException {
        final Email mail = new SimpleEmail();
        mail.setHostName(configuration.getSMTPServer());
        mail.setSmtpPort(configuration.getSMTPPort());
        mail.setAuthenticator(new DefaultAuthenticator(configuration.getSMTPUser(), configuration.getSMTPPassword()));
        mail.setSSLOnConnect(configuration.getSMTPSSL());
        mail.setFrom(configuration.getSMTPFrom());
        mail.setSubject("Your challenge to login to SportChef");
        mail.setMsg(String.format("Challenge = %s", challenge));
        mail.addTo(email);
        mail.send();
    }

    public Optional<String> validateChallenge(@NotNull final Identity identity,
                                              @NotNull final DefaultLoginCredentials credential) {
        Optional<String> token = Optional.empty();

        if (!identity.isLoggedIn()) {
            final String email = credential.getUserId();
            final String challenge = credential.getPassword();

            final String cachedChallenge = challengeCache.getIfPresent(email);
            if (challenge.equals(cachedChallenge)) {
                token = generateToken(email);
            }
        }

        return token;
    }

    public Optional<String> authentication(@NotNull final Identity identity,
                                           @NotNull final DefaultLoginCredentials credentials,
                                           @NotNull final String token) {
        if (!identity.isLoggedIn()) {
            final SimpleTokenCredential credential = new SimpleTokenCredential(token);
            credentials.setCredential(credential);
            identity.login();
        }

        return identity.getAccount() != null ? Optional.of(token) : Optional.empty();
    }

    public Optional<String> generateToken(@NotNull final String email) {
        String token = null;

        try {
            final Optional<User> user = userService.findByEmail(email);
            if (user.isPresent()) {
                final LocalDateTime expiration = LocalDateTime.now().plus(TOKEN_EXPIRATION_TIME);
                final Instant expirationInstant = expiration.atZone(ZoneId.systemDefault()).toInstant();
                final Date expirationDate = Date.from(expirationInstant);

                final ObjectMapper mapper = new ObjectMapper();
                final String payload = mapper.writeValueAsString(user.get());

                token = Jwts.builder()
                        .setSubject("SportChef")
                        .setPayload(payload)
                        .setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS512, jwtSigningKey)
                        .compact();
            }
        } catch (@NotNull final JsonProcessingException e) {
            LOGGER.severe(String.format("Unable to map user object to JSON: %s", e.getMessage()));
        }

        return Optional.ofNullable(token);
    }

    public void logout(@NotNull final Identity identity) {
        if (identity.isLoggedIn()) {
            identity.logout();
        }
    }

}
