package ch.sportchef.business.authentication.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class Challenge {

    private final String challenge;
    private int tries = 0;

    public void increaseTries() {
        tries++;
    }
}
