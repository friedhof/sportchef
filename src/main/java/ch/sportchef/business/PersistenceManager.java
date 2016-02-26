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
package ch.sportchef.business;

import pl.setblack.airomem.core.SimpleController;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public enum PersistenceManager {
    ;

    private static final String SPORTCHEF_DIRECTORY_NAME = ".sportchef"; //NON-NLS
    private static final String PREVAYLER_DIRECTORY_NAME = "prevayler"; //NON-NLS

    private static final Path DATA_DIRECTORY;

    static {
        DATA_DIRECTORY = Paths.get( SPORTCHEF_DIRECTORY_NAME, PREVAYLER_DIRECTORY_NAME);
    }

    public static <T extends Serializable> SimpleController<T> createSimpleController(
            final Class<? extends Serializable> clazz, final Supplier<T> constructor) {
        final String dir = DATA_DIRECTORY.resolve(clazz.getName()).toString();
        return SimpleController.loadOptional(dir, constructor);
    }

}
