package hartman.games.adventureland.api;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private static final String GAME_REL = "game";
    private static final String TAKE_TURN_REL = "takeTurn";

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
        return mvc.perform(get("/")). //
                andExpect(status().isOk()). //
                andExpect(linkWithRelIsPresent(ADVENTURES_REL)). //
                andReturn().getResponse();
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

    @Test
    public void uploadAdventureScript() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        verifyScript(response);
    }

    @Test
    public void startNewGame() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        response = createNewGame(response);
        verifyGame(response);
    }

    @Test
    public void takeTurn() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        response = createNewGame(response);
        response = postNewTurn(response);
        verifyTurn(response);
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
     * Follows the {@code adventure} link and asserts response has the self, games, start and upload links.
     */
    private void verifyAdventure(MockHttpServletResponse response) throws Exception {

        Link adventureLink = getDiscovererFor(response).findLinkWithRel(ADVENTURE_REL, response.getContentAsString());

        mvc.perform(get(adventureLink.expand().getHref())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andExpect(linkWithRelIsPresent(GAMES_REL)).
                andExpect(linkWithRelIsPresent(START_REL)).
                andExpect(linkWithRelIsPresent(UPLOAD_REL)).
                andExpect(jsonPath("$.title", is("Archie's Great Escape"))).
                andExpect(jsonPath("$.author", is("Archie Hartman"))).
                andExpect(jsonPath("$.publishedDate", is("2018-08-31"))).
                andExpect(jsonPath("$.version", is("0.0.1"))).
                andExpect(jsonPath("$.script").doesNotExist()).
                andExpect(jsonPath("$.games").doesNotExist()).
                andReturn().getResponse();
    }

    /**
     * Posts an adventure script file to the upload link of an adventure resource.
     * Then follows the URI returned from the Location header.
     */
    private MockHttpServletResponse uploadAdventureScript(MockHttpServletResponse response) throws Exception {

        Link uploadLink = getDiscovererFor(response)
                .findLinkWithRel(UPLOAD_REL, response.getContentAsString());

        ClassPathResource resource = new ClassPathResource("adventure.txt");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "adventure.txt", "text/plain", resource.getInputStream());

        MockHttpServletResponse result = mvc
                .perform(
                        multipart(uploadLink.expand().getHref())
                                .file(mockMultipartFile)
                                .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        return mvc.perform(get(result.getHeader("Location"))).andReturn().getResponse();
    }

    private void verifyScript(MockHttpServletResponse response) throws Exception {

        Link selfLink = getDiscovererFor(response).findLinkWithRel(Link.REL_SELF, response.getContentAsString());

        mvc.perform(get(selfLink.expand().getHref())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andExpect(linkWithRelIsPresent(ADVENTURE_REL)).
                andReturn().getResponse();
    }

    /**
     * Follow the adventure link.
     * Post to the games link
     */
    private MockHttpServletResponse createNewGame(MockHttpServletResponse response) throws Exception {

        Link adventureLink = getDiscovererFor(response).findLinkWithRel(ADVENTURE_REL, response.getContentAsString());

        response = mvc.perform(get(adventureLink.expand().getHref())).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andExpect(linkWithRelIsPresent(GAMES_REL)).
                andReturn().getResponse();

        ClassPathResource resource = new ClassPathResource("game.json");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());

        Link gamesLink = getDiscovererFor(response).findLinkWithRel(GAMES_REL, response.getContentAsString());

        response = mvc.perform(
                post(gamesLink.expand().getHref())
                        .content(data)
                        .contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andReturn().getResponse();

        return mvc.perform(get(response.getHeader("Location"))).andReturn().getResponse();
    }

    private void verifyGame(MockHttpServletResponse response) throws Exception {

        Link selfLink = getDiscovererFor(response).findLinkWithRel(Link.REL_SELF, response.getContentAsString());

        mvc.perform(get(selfLink.expand().getHref())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andExpect(linkWithRelIsPresent(ADVENTURE_REL)).
                andExpect(jsonPath("$.player", is("Michael"))).
                andExpect(jsonPath("$.startTime").exists()).
                andExpect(jsonPath("$.status", is("Ready"))).
                andReturn().getResponse();
    }

    private MockHttpServletResponse postNewTurn(MockHttpServletResponse source) throws Exception {

        Link gameLink = getDiscovererFor(source).findLinkWithRel(Link.REL_SELF, source.getContentAsString());

        MockHttpServletResponse response = mvc.perform(get(gameLink.expand().getHref()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Link taketurnLink = getDiscovererFor(response).findLinkWithRel(TAKE_TURN_REL, response.getContentAsString());

        ClassPathResource resource = new ClassPathResource("turn.json");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());

        response = mvc.perform(
                post(taketurnLink.expand().getHref())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)).
                andExpect(status().isOk()).
                andReturn().getResponse();

        return response;
    }

    private void verifyTurn(MockHttpServletResponse response) throws Exception {

        Link selfLink = getDiscovererFor(response).findLinkWithRel(Link.REL_SELF, response.getContentAsString());

        mvc.perform(get(selfLink.expand().getHref())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(Link.REL_SELF)).
                andExpect(linkWithRelIsPresent(GAME_REL)).
                andExpect(jsonPath("$.command", is("look"))).
                andExpect(jsonPath("$.timestamp").exists()).
                andExpect(jsonPath("$.output").exists()).
                andReturn().getResponse();
    }

}
