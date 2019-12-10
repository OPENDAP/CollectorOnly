/**
 Copyright (c) 2019 OPeNDAP, Inc.
 Please read the full copyright statement in the file LICENSE.

 Authors: 
	James Gallagher	 <jgallagher@opendap.org>
    Samuel Lloyd	 <slloyd@opendap.org>

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 You can contact OPeNDAP, Inc. at PO Box 112, Saunderstown, RI. 02874-0112.
*/

/**
 * DB Connection configuration
 */
package org.opendap.harvester.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.opendap.harvester.configuration.springdatajava8.LocalDateTimeToStringConverter;
import org.opendap.harvester.configuration.springdatajava8.StringToLocalDateTimeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SpringMongoDataConfig extends AbstractMongoConfiguration {
    @Value("${mongo.db.name}")
    private String databaseName;

    @Value("${mongo.db.server}")
    private String databaseServer;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(databaseServer);
    }

    /**
     * Adding custom LocalDate and LocalTime converters for Mongo DAO.
     * @return
     */
    @Bean
    @Override
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new LocalDateTimeToStringConverter());
        converterList.add(new StringToLocalDateTimeConverter());
        return new CustomConversions(converterList);
    }
}