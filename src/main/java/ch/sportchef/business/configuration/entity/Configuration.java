/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015, 2016 Marcus Fihlon
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
package ch.sportchef.business.configuration.entity;

import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Properties;

@ToString
public class Configuration {

    private static final String TOKEN_SIGNING_KEY = "token.signing.key";

    private static final String ADMIN_PASSWORD_KEY = "admin.password";
    private static final String ADMIN_FIRSTNAME_KEY = "admin.firstname";
    private static final String ADMIN_LASTNAME_KEY = "admin.lastname";
    private static final String ADMIN_EMAIL_KEY = "admin.email";
    private static final String ADMIN_PHONE_KEY = "admin.phone";

    private final Properties properties = new Properties();

    public Configuration(@NotNull final Map<Object, Object> properties) {
        properties.forEach(this.properties::put);
    }

    public String getTokenSigningKey() {
        return properties.getProperty(TOKEN_SIGNING_KEY);
    }

    public String getAdminPassword() {
        return properties.getProperty(ADMIN_PASSWORD_KEY);
    }

    public String getAdminFirstname() {
        return properties.getProperty(ADMIN_FIRSTNAME_KEY);
    }

    public String getAdminLastname() {
        return properties.getProperty(ADMIN_LASTNAME_KEY);
    }

    public String getAdminEmail() {
        return properties.getProperty(ADMIN_EMAIL_KEY);
    }

    public String getAdminPhone() {
        return properties.getProperty(ADMIN_PHONE_KEY);
    }

    public String getSMTPServer() {
        return properties.getProperty("smtp.server", null);
    }

    public Integer getSMTPPort() {
        return Integer.valueOf(properties.getProperty("smtp.port", "25"));
    }

    public String getSMTPUser() {
        return properties.getProperty("smtp.user", null);
    }

    public String getSMTPPassword() {
        return properties.getProperty("smtp.password", null);
    }

    public Boolean getSMTPSSL() {
        return Boolean.valueOf(properties.getProperty("smtp.useSSL", "false"));
    }

    public String getSMTPFrom() {
        return properties.getProperty("smtp.from", null);
    }

}
