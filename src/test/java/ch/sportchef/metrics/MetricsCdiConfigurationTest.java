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
package ch.sportchef.metrics;

import io.astefanutti.metrics.cdi.MetricsConfiguration;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MetricsCdiConfigurationTest {

    @Test
    public void configure() {
        // arrange
        final MetricsConfiguration metricsConfigurationMock = mock(MetricsConfiguration.class);
        final MetricsCdiConfiguration metricsCdiConfiguration = new MetricsCdiConfiguration();

        // act
        metricsCdiConfiguration.configure(metricsConfigurationMock);

        // assert
        verify(metricsConfigurationMock, times(1)).useAbsoluteName(true);
    }

}