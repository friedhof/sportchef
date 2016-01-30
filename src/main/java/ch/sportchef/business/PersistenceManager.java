package ch.sportchef.business;

import pl.setblack.airomem.core.SimpleController;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public enum PersistenceManager {
    ;

    private static final String USER_HOME_PROPERTY_KEY = "user.home"; //NON-NLS
    private static final String SPORTCHEF_DIRECTORY_NAME = ".sportchef"; //NON-NLS
    private static final String PREVAYLER_DIRECTORY_NAME = "prevayler"; //NON-NLS

    private static final Path DATA_DIRECTORY;

    static {
        @SuppressWarnings("AccessOfSystemProperties")
        final String userHome = System.getProperty(USER_HOME_PROPERTY_KEY);
        DATA_DIRECTORY = Paths.get(userHome, SPORTCHEF_DIRECTORY_NAME, PREVAYLER_DIRECTORY_NAME);
    }

    public static <T extends Serializable> SimpleController<T> createSimpleController(
            final Class<? extends Serializable> clazz, final Supplier<T> constructor) {
        final String dir = DATA_DIRECTORY.resolve(clazz.getName()).toString();
        return SimpleController.loadOptional(dir, constructor);
    }

}
