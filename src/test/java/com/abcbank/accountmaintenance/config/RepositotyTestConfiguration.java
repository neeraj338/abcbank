package com.abcbank.accountmaintenance.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.abcbank.accountmaintenance.repository.AccountRepository;
import com.abcbank.accountmaintenance.repository.TransactionRepository;

@Profile("test")
@Configuration
public class RepositotyTestConfiguration {

    @Bean
    @Primary
	public AccountRepository accountRepo() {
		return Mockito.mock(AccountRepository.class);
    }

	@Bean
	@Primary
	public TransactionRepository txRepositoty() {
		return Mockito.mock(TransactionRepository.class);
	}

}