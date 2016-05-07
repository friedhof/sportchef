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

import ch.sportchef.business.authentication.entity.Challenge;
import ch.sportchef.business.authentication.entity.Role;
import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Singleton
@Timed(name = "Timed: AuthenticationService")
@Metered(name = "Metered: AuthenticationService")
public class AuthenticationService {

    private static final String CHALLENGE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MINIMAL_CHALLENGE_LENGTH = 5;
    private static final int MAXIMAL_CHALLENGE_LENGTH = 10;
    private static final int TRESHOLD_FOR_COMPLEXITY_INCREASE = 20;
    private static final long TOKEN_EXPIRATION_TIME_IN_MS = 8 * 60 * 60 * 1000; // 8 hours
    private static final int MAXIMAL_WRONG_CHALENGE_TRIES = 10;

    private UserService userService;
    private ConfigurationService configurationService;

    private Cache<String, Challenge> challengeCache;

    public AuthenticationService(UserService userService, ConfigurationService configurationService) {
        this.userService = userService;
        this.configurationService = configurationService;
        challengeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public boolean requestChallenge(@NotNull final String email) {
        final Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            final Challenge challenge = generateChallenge();
            challengeCache.put(email, challenge);
            sendChallenge(email, challenge);
            return true;
        }
        return false;
    }

    private Challenge generateChallenge() {
        final int requiredLength = currentlyRequiredChallengeLength();
        final StringBuilder challengeBuilder = new StringBuilder(requiredLength);
        final Random random = new SecureRandom();
        while (challengeBuilder.length() < requiredLength) {
            final char randomChar = CHALLENGE_CHARACTERS.charAt(random.nextInt(CHALLENGE_CHARACTERS.length()));
            challengeBuilder.append(randomChar);
        }
        return new Challenge(challengeBuilder.toString());
    }

    private int currentlyRequiredChallengeLength() {
        @SuppressWarnings("NumericCastThatLosesPrecision")
        final int complexityIncrease = (int) (challengeCache.size() / TRESHOLD_FOR_COMPLEXITY_INCREASE);
        final int calculatedComplexity = MINIMAL_CHALLENGE_LENGTH + complexityIncrease;
        return Math.min(MAXIMAL_CHALLENGE_LENGTH, calculatedComplexity);
    }

    @SneakyThrows
    private void sendChallenge(@NotNull final String email, @NotNull final Challenge challenge) {
        final Configuration configuration = configurationService.getConfiguration();
        final Email mail = new SimpleEmail();
        mail.setHostName(configuration.getSMTPServer());
        mail.setSmtpPort(configuration.getSMTPPort());
        mail.setAuthenticator(new DefaultAuthenticator(configuration.getSMTPUser(), configuration.getSMTPPassword()));
        mail.setSSLOnConnect(configuration.getSMTPSSL());
        mail.setFrom(configuration.getSMTPFrom());
        mail.setSubject("Your challenge to login to SportChef");
        mail.setMsg(String.format("Challenge = %s", challenge.getChallenge()));
        mail.addTo(email);
        mail.send();
    }

    public Optional<String> validateChallenge(@NotNull final String email,
                                              @NotNull final String challengeValue) {
        final Challenge cachedChallenge = challengeCache.getIfPresent(email);
        if (cachedChallenge != null) {
            if (challengeValue.equals(cachedChallenge.getChallenge()) && cachedChallenge.getTries() < MAXIMAL_WRONG_CHALENGE_TRIES) {
                challengeCache.invalidate(email);
                return Optional.of(generateToken(email));
            } else {
                cachedChallenge.increaseTries();
            }
        }

        return Optional.empty();
    }

    private String generateToken(@NotNull final String email) {
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + TOKEN_EXPIRATION_TIME_IN_MS);

        final Claims claims = Jwts.claims();
        claims.setIssuedAt(now);
        claims.setExpiration(exp);
        claims.put("email", email); //NON-NLS

        final Configuration configuration = configurationService.getConfiguration();
        final String tokenSigningKey = configuration.getTokenSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, tokenSigningKey)
                .compact();
    }

    Optional<User> validate(@NotNull final String token) {
        final Configuration configuration = configurationService.getConfiguration();
        final String tokenSigningKey = configuration.getTokenSigningKey();

        final Jws<Claims> result = Jwts.parser()
                .setSigningKey(tokenSigningKey)
                .parseClaimsJws(token);
        final Claims claims = result.getBody();
        final String email = claims.get("email", String.class); //NON-NLS
        return userService.findByEmail(email);
    }

    public boolean isUserInRole(@NotNull final User user, @NotNull final Role role) {
        return user.getRole().getLevel() >= role.getLevel();
    }
}
