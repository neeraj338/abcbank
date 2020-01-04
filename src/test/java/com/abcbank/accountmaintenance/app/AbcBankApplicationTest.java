package com.abcbank.accountmaintenance.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.abcbank.accountmaintenance.app.AbcBankApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbcBankApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "test" })
public class AbcBankApplicationTest {
	
	@LocalServerPort
	int randomServerPort;
	
	@Test
	public void sampleTest() {
		// to insure application has all dependency and ready to launch
	}
}
