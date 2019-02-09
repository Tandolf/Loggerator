package se.andolf.loggerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import se.andolf.loggerator.model.AddBody;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoggeratorApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldGetOne() {
		final AddBody build = AddBody.builder().addendOne(3).addendTwo(5).build();
		final ResponseEntity<String> response = restTemplate.postForEntity("/one", build, String.class);
		assertEquals("8", response.getBody());
	}

	@Test
	public void shouldGetTwo() {
		final ResponseEntity<String> response = restTemplate.getForEntity("/two", String.class);
		assertEquals("2", response.getBody());
	}

}

