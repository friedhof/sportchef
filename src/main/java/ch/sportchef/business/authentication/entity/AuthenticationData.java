package ch.sportchef.business.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.persistence.Entity;

@Entity
@Value
@AllArgsConstructor
public class AuthenticationData {

    private String email;
    private String challenge;

}
