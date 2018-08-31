package hartman.games.adventureland.api;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdventuresResourceWebIntegrationTest extends AbstractWebIntegrationTest {

    private static final String ADVENTURES_REL = "adventures";
    private static final String GAMES_REL = "games";
    private static final String START_REL = "start";
    private static final String UPLOAD_REL = "upload";
    private static final String ADVENTURE_REL = "adventure";

    @Test
    public void exposesAdventuresResourceViaRootResource() throws Exception {
        mvc.perform(get("/")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON)).
                andExpect(jsonPath("$._links.adventures.href", notNullValue())).
                andExpect(jsonPath("$._links.games.href").doesNotExist());
    }

    private MockHttpServletResponse accessRootResource() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/")). //
                andExpect(status().isOk()). //
                andExpect(linkWithRelIsPresent(ADVENTURES_REL)). //
                andReturn().getResponse();

        return response;
    }


    /**
     * Creates a new {@link Adventure}
     */
    @Test
    public void createNewAdventure() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        verifyAdventure(response);
    }

    /**
     * - Creates a new {@link Adventure} by looking up the adventure link posting the content of adventure.json.
     * - Verifies we receive a {@code 201 Created} and a {@code Location} header.
     * - Follows the location header to retrieve and verify the {@link Adventure} just created.
     */
    private MockHttpServletResponse createNewAdventure(MockHttpServletResponse source) throws Exception {

        String content = source.getContentAsString();

        Link adventuresLink = getDiscovererFor(source).findLinkWithRel(ADVENTURES_REL, content);

        ClassPathResource resource = new ClassPathResource("adventure.json");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());

        MockHttpServletResponse response =
                mvc.perform(
                        post(adventuresLink.expand().getHref())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(data)).
                        andExpect(status().isCreated()).
                        andExpect(header().string("Location", is(Matchers.notNullValue()))).
                        andReturn().getResponse();

        return mvc.perform(get(response.getHeader("Location"))).andReturn().getResponse();
    }


    /**
     * Follows the {@code order} link and asserts only the self link being present so that no further navigation is
     * possible anymore.
     */
    private void verifyAdventure(MockHttpServletResponse response) throws Exception {

        Link adventureLink = getDiscovererFor(response)
                .findLinkWithRel(ADVENTURE_REL, response.getContentAsString());

        mvc.perform(get(adventureLink.expand().getHref())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andExpect(linkWithRelIsPresent(GAMES_REL)).
                andExpect(linkWithRelIsPresent(START_REL)).
                andExpect(linkWithRelIsPresent(UPLOAD_REL)).
                andReturn().getResponse();
    }

}
