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

import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import pl.setblack.badass.Politician;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Singleton
public class AuthenticationService {

    private static final String CHALLENGE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CHALLENGE_LENGTH = 10;
    private static final TemporalAmount TOKEN_EXPIRATION_TIME = Duration.ofDays(1);

    @Inject
    private UserService userService;

    @Inject
    private ConfigurationService configurationService;

    private Cache<String, String> challengeCache;

    private RsaJsonWebKey rsaJsonWebKey;

    @PostConstruct
    private void init() throws JoseException {
        challengeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    }

    public boolean requestChallenge(@NotNull final String email) {
        final Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            final String challenge = generateChallenge();
            challengeCache.put(email, challenge);
            Politician.beatAroundTheBush(() ->
                    sendChallenge(email, challenge));
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
            token = Optional.of(generateToken(email));
        }

        return token;
    }

    public String generateToken(@NotNull final String email) {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(email);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return Politician.beatAroundTheBush(() -> jws.getCompactSerialization());
    }
}
