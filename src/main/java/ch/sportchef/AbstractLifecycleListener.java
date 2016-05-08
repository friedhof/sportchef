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
package ch.sportchef;

import org.eclipse.jetty.util.component.LifeCycle;

import javax.validation.constraints.NotNull;

public class AbstractLifecycleListener implements LifeCycle.Listener {

    @Override
    public void lifeCycleStarting(@NotNull final LifeCycle event) {
    }

    @Override
    public void lifeCycleStarted(@NotNull final LifeCycle event) {
    }

    @Override
    public void lifeCycleFailure(@NotNull final LifeCycle event, @NotNull final Throwable cause) {
    }

    @Override
    public void lifeCycleStopping(@NotNull final LifeCycle event) {
    }

    @Override
    public void lifeCycleStopped(@NotNull final LifeCycle event) {
    }

}
