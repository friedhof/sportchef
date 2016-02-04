/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.auth.service;

import ch.sportchef.business.configuration.boundary.ConfigurationManager;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.entity.User;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Named
public class LoginService {

    @Inject
    private ConfigurationManager configurationManager;

    private static HashMap<String,User> loginUsers = new HashMap<>(); // Pending requests
    private static HashMap<String,User> activeUsers = new HashMap<>(); // Active (sessions)

    public void LoginService(){
        System.out.println("LoginService - Init");
    }

    /**
     * Set User and Login-Token
     * @param token
     * @param user
     */
    public static void setLoginRequest(String token, User user){
        loginUsers.put(token, user);
    }

    /**
     * Get User by Login-Token
     * @param token
     * @return User
     */
    public static User getLoginRequest(String token){
        return loginUsers.get(token);
    }

    /**
     * Set User and Session-Token
     * @param token
     * @param user
     */
    public static void setUser(String token, User user){
        activeUsers.put(token, user);
    }

    /**
     * Get User by Session-Token
     * @param token
     * @return User
     */
    public static User getUser(String token){
        return activeUsers.get(token);
    }

    public User checkLoginToken(String token, HashMap<String,String> cookie) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        if(generateMailToken(cookie.get("token"),cookie.get("email")).equals(token)){
            /**
             * @ToDo: check exception
             */
            try{
                return getLoginRequest(token);
            } catch(Exception e){
            }
        }
        return null;
    }

    public String generateToken(){
        String token = UUID.randomUUID().toString();
        return token;
    }

    public String loginRequest(User user) throws EmailException, InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final Configuration configuration = configurationManager.getConfiguration();

        String loginToken = generateToken();
        String mailToken = generateMailToken(loginToken,user.getEmail());
        setLoginRequest(mailToken,user);

        /**
         * @ToDo: change mailMessage /@PReimers (2015-12-29)
         */
        String mailMessage = "Hello "+user.getFirstName()+"\r\n\r\n"+"To complete your login please click the following link:"+"\r\n\r\n"+"Link: "+configuration.getAppDomain()+"/authenticate.html?token="+URLEncoder.encode(mailToken,"UTF-8")+"\r\n\r\n"+"This link is valid for 10 minutes.";

        sendMail(user.getEmail(),"Sportchef - Login",mailMessage);

        return loginToken;
    }

    private static String generateMailToken(String token, String email) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        byte[] emailByteArray = email.getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
            token.toCharArray(), emailByteArray, (20*1000), 256)
        );
        return Base64.encodeBase64String(key.getEncoded());
    }

    public Boolean sendMail(String recipient, String subject, String message) throws EmailException {
        final Configuration configuration = configurationManager.getConfiguration();

        Email email = new SimpleEmail();
        email.setHostName(configuration.getSMTPServer());
        email.setSmtpPort(configuration.getSMTPPort());
        email.setAuthenticator(new DefaultAuthenticator(configuration.getSMTPUser(), configuration.getSMTPPassword()));
        email.setSSLOnConnect(configuration.getSMTPSSL());
        email.setFrom(configuration.getSMTPFrom());
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(recipient);
        email.send();

        return true;
    }
}
