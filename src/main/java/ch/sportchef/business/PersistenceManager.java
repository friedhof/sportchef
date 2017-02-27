/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016, 2017 Marcus Fihlon
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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pl.setblack.airomem.core.PersistenceController;
import pl.setblack.airomem.core.builders.PrevaylerBuilder;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

@Slf4j
@UtilityClass
public class PersistenceManager {

    public static <T extends Serializable> PersistenceController<T> createController(
            final Class<? extends Serializable> clazz, final Supplier<T> constructor) {

        final String homeDir = System.getProperty("user.home");
        final Path path = Paths.get(homeDir, ".sportchef", "data", clazz.getName());
        log.info("Using persistence store '{}' for entity '{}'.", path, clazz.getName());

        return PrevaylerBuilder
                .<T>newBuilder()
                .withFolder(path)
                .useSupplier(constructor)
                .build();
    }

}
