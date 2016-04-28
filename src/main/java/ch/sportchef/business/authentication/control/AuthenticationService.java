/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.authentication.control;

import ch.sportchef.business.authentication.entity.Role;
import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.metrics.healthcheck.AuthenticationServiceHealthCheck;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Singleton
@Timed(name = "Timed: AuthenticationService")
@Metered(name = "Metered: AuthenticationService")
public class AuthenticationService {

    private static final String CHALLENGE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CHALLENGE_LENGTH = 10;
    private static final long TOKEN_EXPIRATION_TIME_IN_MILLISECONDS = 8 * 60 * 60 * 1000; // 8 hours

    @Inject
    private UserService userService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private HealthCheckRegistry healthCheckRegistry;

    private Cache<String, String> challengeCache;

    @PostConstruct
    @SneakyThrows
    private void init() {
        challengeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        final AuthenticationServiceHealthCheck authenticationServiceHealthCheck = new AuthenticationServiceHealthCheck(this);
        healthCheckRegistry.register(AuthenticationService.class.getName(), authenticationServiceHealthCheck);
    }

    @SneakyThrows
    public boolean requestChallenge(@NotNull final String email) {
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
        final Configuration configuration = configurationService.getConfiguration();
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

    public Optional<String> validateChallenge(@NotNull final String email,
                                              @NotNull final String challenge) {
        Optional<String> token = Optional.empty();

        final String cachedChallenge = challengeCache.getIfPresent(email);
        if (challenge.equals(cachedChallenge)) {
            challengeCache.invalidate(email);
            token = Optional.of(generateToken(email));
        }

        return token;
    }

    private String generateToken(@NotNull final String email) {
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + TOKEN_EXPIRATION_TIME_IN_MILLISECONDS);

        final Claims claims = Jwts.claims();
        claims.setIssuedAt(now);
        claims.setExpiration(exp);
        claims.put("email", email);

        final Configuration configuration = configurationService.getConfiguration();
        final String tokenSigningKey = configuration.getTokenSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, tokenSigningKey)
                .compact();
    }

    Optional<User> validate(@NotNull String token) {
        final Configuration configuration = configurationService.getConfiguration();
        final String tokenSigningKey = configuration.getTokenSigningKey();

        final Jws<Claims> result = Jwts.parser()
                .setSigningKey(tokenSigningKey)
                .parseClaimsJws(token.replace("Bearer ", ""));
        final Claims claims = result.getBody();
        final String email = claims.get("email", String.class);
        return userService.findByEmail(email);
    }

    public boolean isUserInRole(@NotNull final User user, @NotNull final Role role) {
        return user.getRole().getLevel() >= role.getLevel();
    }
}
