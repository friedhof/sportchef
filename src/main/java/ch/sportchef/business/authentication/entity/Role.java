package ch.sportchef.business.authentication.entity;

public enum Role {

    ADMIN(1000),
    USER(100);

    private int level;

    Role(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
