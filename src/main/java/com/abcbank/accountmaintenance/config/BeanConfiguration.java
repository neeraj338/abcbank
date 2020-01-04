package com.abcbank.accountmaintenance.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class BeanConfiguration {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE)
				.setMatchingStrategy(MatchingStrategies.LOOSE)
				.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR)
				.setDestinationNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);

		return modelMapper;
	}

}
