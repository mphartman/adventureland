package hartman.games.adventureland.api;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class to derive concrete web test classes from.
 * 
 * @author https://github.com/olivergierke/spring-restbucks
 */
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // to disable use of in-memory database for JPA integration test
@FlywayTest
@SpringBootTest
public abstract class AbstractWebIntegrationTest {

	@Autowired WebApplicationContext context;
	@Autowired LinkDiscoverers links;

	protected MockMvc mvc;

	@BeforeEach
	public void setUp() {

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				defaultRequest(MockMvcRequestBuilders.get("/").locale(Locale.US)).//
				apply(SecurityMockMvcConfigurers.springSecurity()).
				build();
	}

	/**
	 * Creates a {@link ResultMatcher} that checks for the presence of a link with the given rel.
	 * 
	 * @param rel
	 * @return
	 */
	protected ResultMatcher linkWithRelIsPresent(final String rel) {
		return new LinkWithRelMatcher(rel, true);
	}

	/**
	 * Creates a {@link ResultMatcher} that checks for the non-presence of a link with the given rel.
	 * 
	 * @param rel
	 * @return
	 */
	protected ResultMatcher linkWithRelIsNotPresent(String rel) {
		return new LinkWithRelMatcher(rel, false);
	}

	protected LinkDiscoverer getDiscovererFor(MockHttpServletResponse response) {
		Assert.notNull(response.getContentType(), "Response.getContentType cannot be null");
		Optional<LinkDiscoverer> linkDiscovererFor = links.getLinkDiscovererFor(response.getContentType());
		if (linkDiscovererFor.isPresent()) {
			return linkDiscovererFor.get();
		}
		else {
			throw new IllegalArgumentException("No LinkDiscoverer found for " + response.getContentType());
		}
	}

	private class LinkWithRelMatcher implements ResultMatcher {

		private final String rel;
		private final boolean present;

		public LinkWithRelMatcher(String rel, boolean present) {
			this.rel = rel;
			this.present = present;
		}

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.test.web.servlet.ResultMatcher#match(org.springframework.test.web.servlet.MvcResult)
		 */
		@Override
		public void match(MvcResult result) throws Exception {

			MockHttpServletResponse response = result.getResponse();
			String content = response.getContentAsString();
			assertThat(response.getContentType()).isNotNull();
			Optional<LinkDiscoverer> discoverer = links.getLinkDiscovererFor(response.getContentType());

			assertThat(discoverer).isPresent();
			assertThat(discoverer.get().findLinkWithRel(rel, content)).matches(it -> present == (it != null && it.isPresent()));
		}
	}
}
