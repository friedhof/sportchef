package ch.sportchef.business.authentication.entity;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class Challenge {

    private final String challenge;
    private int tries = 0;

    public Challenge(String challenge) {
        this.challenge = challenge;
    }

    public void increaseTries() {
        tries++;
    }
}
