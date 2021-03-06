package hartman.games.adventureland.api;

import hartman.games.adventureland.api.security.WithMockToken;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdventurelandWebIT extends AbstractWebIntegrationTest {

    private static final String ADVENTURES_REL = "adventures";
    private static final String GAMES_REL = "games";
    private static final String START_REL = "start";
    private static final String UPLOAD_REL = "upload";
    private static final String ADVENTURE_REL = "adventure";
    private static final String GAME_REL = "game";
    private static final String TAKE_TURN_REL = "takeTurn";
    private static final String TURNS_REL = "turns";

    @Test
    @WithMockToken
    public void exposesAdventuresResourceViaRootResource() throws Exception {
        mvc.perform(get("/")).
                andDo(print()).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON)).
                andExpect(jsonPath("$._links.adventures.href", notNullValue())).
                andExpect(jsonPath("$._links.games.href").doesNotExist());
    }

    /**
     * Creates a new {@link Adventure}
     */
    @Test
    @WithMockToken
    public void createNewAdventure() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        verifyAdventure(response);
    }

    /**
     * Returns the response from the root.
     */
    private MockHttpServletResponse accessRootResource() throws Exception {
        return mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent(ADVENTURES_REL))
                .andReturn()
                .getResponse();
    }

    /**
     * - Creates a new {@link Adventure} by looking up the adventure link posting the content of adventure.json.
     * - Verifies we receive a {@code 201 Created} and a {@code Location} header.
     * - Follows the location header to retrieve and verify the {@link Adventure} just created.
     */
    private MockHttpServletResponse createNewAdventure(MockHttpServletResponse source) throws Exception {

        String content = source.getContentAsString();

        Link adventuresLink = getDiscovererFor(source).findLinkWithRel(ADVENTURES_REL, content).orElseThrow(AssertionError::new);

        ClassPathResource resource = new ClassPathResource("data/adventure.json");
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

        Link adventureLink = getDiscovererFor(response).findLinkWithRel(ADVENTURE_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        mvc.perform(get(adventureLink.expand().getHref())).
                andDo(print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value())).
                andExpect(linkWithRelIsPresent(GAMES_REL)).
                andExpect(linkWithRelIsNotPresent(START_REL)).
                andExpect(linkWithRelIsPresent(UPLOAD_REL)).
                andExpect(jsonPath("$.title", is("Archie's Great Escape"))).
                andExpect(jsonPath("$.author", is("Archie Hartman"))).
                andExpect(jsonPath("$.publishedDate", is("2018-08-31"))).
                andExpect(jsonPath("$.version", is("0.0.1"))).
                andExpect(jsonPath("$.script").doesNotExist()).
                andExpect(jsonPath("$.games").doesNotExist());
    }

    /**
     * Update an existing {@link Adventure}
     */
    @Test
    @WithMockToken
    public void updateExistingAdventure() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = updateAdventure(response);
        verifyUpdatedAdventure(response);
    }

    /**
     * - Follows adventure link to retrieve Adventure resource
     * - Does a PUT
     * - Verifies we receive a {@code 204 No Content} and a {@code Location} header.
     * - Follows the Location header to retrieve the updated {@link Adventure}.
     */
    private MockHttpServletResponse updateAdventure(MockHttpServletResponse source) throws Exception {

        String content = source.getContentAsString();

        Link adventureLink = getDiscovererFor(source).findLinkWithRel(ADVENTURE_REL, content).orElseThrow(AssertionError::new);

        byte[] data = Files.readAllBytes(new ClassPathResource("data/adventure_updated.json").getFile().toPath());

        MockHttpServletResponse response =
                mvc.perform(
                        put(adventureLink.expand().getHref())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(data)).
                        andDo(print()).
                        andExpect(status().isNoContent()).
                        andExpect(header().string("Location", is(Matchers.notNullValue()))).
                        andReturn().getResponse();

        return mvc.perform(get(response.getHeader("Location"))).andReturn().getResponse();
    }

    /**
     * Follows the {@code adventure} link and asserts response has the self, games, start and upload links.
     */
    private void verifyUpdatedAdventure(MockHttpServletResponse response) throws Exception {

        Link adventureLink = getDiscovererFor(response).findLinkWithRel(ADVENTURE_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        mvc.perform(get(adventureLink.expand().getHref())).
                andDo(print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value())).
                andExpect(linkWithRelIsPresent(GAMES_REL)).
                andExpect(linkWithRelIsNotPresent(START_REL)).
                andExpect(linkWithRelIsPresent(UPLOAD_REL)).
                andExpect(jsonPath("$.title", is("Archie's Okay Escape"))).
                andExpect(jsonPath("$.author", is("PinkTapir"))).
                andExpect(jsonPath("$.publishedDate", is("2019-08-17"))).
                andExpect(jsonPath("$.version", is("1.0.0"))).
                andExpect(jsonPath("$.script").doesNotExist()).
                andExpect(jsonPath("$.games").doesNotExist());
    }

    @Test
    @WithMockToken
    public void uploadAdventureScript() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        verifyScript(response);
    }

    /**
     * Posts an adventure script file to the upload link of an adventure resource.
     * Then follows the URI returned from the Location header.
     */
    private MockHttpServletResponse uploadAdventureScript(MockHttpServletResponse response) throws Exception {

        Link uploadLink = getDiscovererFor(response).findLinkWithRel(UPLOAD_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        ClassPathResource resource = new ClassPathResource("data/adventure.txt");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "data/adventure.txt", "text/plain", resource.getInputStream());

        MockHttpServletResponse result = mvc
                .perform(
                        multipart(uploadLink.expand().getHref())
                                .file(mockMultipartFile)
                                .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        return mvc.perform(get(result.getHeader("Location"))).andReturn().getResponse();
    }

    /**
     * - Follows the self link of a AdventureScript resource
     * - Verifies the AdventureScript resource body is as expected
     */
    private void verifyScript(MockHttpServletResponse response) throws Exception {

        Link selfLink = getDiscovererFor(response).findLinkWithRel(IanaLinkRelations.SELF.value(), response.getContentAsString()).orElseThrow(AssertionError::new);;

        mvc.perform(get(selfLink.expand().getHref())).
                andDo(print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value())).
                andExpect(linkWithRelIsPresent(ADVENTURE_REL)).
                andReturn().getResponse();
    }

    @Test
    @WithMockToken
    public void startNewGame() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        response = createNewGame(response);
        verifyGame(response);
    }

    /**
     * Follow the adventure link.
     * Post to the games link
     * Get link returned in Location header
     * Return Game response
     */
    private MockHttpServletResponse createNewGame(MockHttpServletResponse response) throws Exception {

        Link adventureLink = getDiscovererFor(response).findLinkWithRel(ADVENTURE_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        response = mvc.perform(get(adventureLink.expand().getHref())).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value())).
                andExpect(linkWithRelIsPresent(GAMES_REL)).
                andReturn().getResponse();

        ClassPathResource resource = new ClassPathResource("data/game.json");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());

        Link gamesLink = getDiscovererFor(response).findLinkWithRel(GAMES_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        response = mvc.perform(
                post(gamesLink.expand().getHref())
                        .content(data)
                        .contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andExpect(content().string("")).
                andReturn().getResponse();

        return mvc.perform(get(response.getHeader("Location"))).andReturn().getResponse();
    }

    private void verifyGame(MockHttpServletResponse response) throws Exception {

        Link selfLink = getDiscovererFor(response).findLinkWithRel(IanaLinkRelations.SELF.value(), response.getContentAsString()).orElseThrow(AssertionError::new);

        mvc.perform(get(selfLink.expand().getHref())).
                andDo(print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value())).
                andExpect(linkWithRelIsPresent(ADVENTURE_REL)).
                andExpect(linkWithRelIsPresent(TURNS_REL)).
                andExpect(linkWithRelIsPresent(TAKE_TURN_REL)).
                andExpect(jsonPath("$.player", is("Michael"))).
                andExpect(jsonPath("$.startTime").exists()).
                andExpect(jsonPath("$.status", is("Running"))).
                andExpect(jsonPath("$.running").doesNotExist()).
                andExpect(jsonPath("$.notGameOver").doesNotExist()).
                andExpect(jsonPath("$.ready").doesNotExist()).
                andReturn().getResponse();
    }

    @Test
    @WithMockToken
    public void takeTurn() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        response = createNewGame(response);
        response = postNewTurn(response);
        verifyTurn(response);
    }

    private MockHttpServletResponse postNewTurn(MockHttpServletResponse source) throws Exception {

        Link gameLink = getDiscovererFor(source).findLinkWithRel(IanaLinkRelations.SELF.value(), source.getContentAsString()).orElseThrow(AssertionError::new);

        MockHttpServletResponse response = mvc.perform(get(gameLink.expand().getHref()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Link taketurnLink = getDiscovererFor(response).findLinkWithRel(TAKE_TURN_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        ClassPathResource resource = new ClassPathResource("data/turn.json");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());

        response = mvc.perform(
                post(taketurnLink.expand().getHref())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)).
                andExpect(status().isCreated()).
                andExpect(jsonPath("$.command", is("look"))).
                andExpect(jsonPath("$.timestamp").exists()).
                andExpect(jsonPath("$.output").exists()).
                andReturn().getResponse();

        return response;
    }

    private void verifyTurn(MockHttpServletResponse response) throws Exception {

        Link selfLink = getDiscovererFor(response).findLinkWithRel(IanaLinkRelations.SELF.value(), response.getContentAsString()).orElseThrow(AssertionError::new);

        mvc.perform(get(selfLink.expand().getHref())).
                andDo(print()).
                andExpect(status().isOk()).
                andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value())).
                andExpect(linkWithRelIsPresent(GAME_REL)).
                andExpect(jsonPath("$.command", is("look"))).
                andExpect(jsonPath("$.timestamp").exists()).
                andExpect(jsonPath("$.output").exists()).
                andReturn().getResponse();
    }

    @Test
    @WithMockToken
    void getAllGameTurns() throws Exception {
        MockHttpServletResponse response = accessRootResource();
        response = createNewAdventure(response);
        response = uploadAdventureScript(response);
        response = createNewGame(response);
        response = postNewTurn(response);
        verifyPaginatedTurns(response);
    }

    private void verifyPaginatedTurns(MockHttpServletResponse response) throws Exception {

        Link gameLink = getDiscovererFor(response).findLinkWithRel(GAME_REL, response.getContentAsString()).orElseThrow(AssertionError::new);

        MockHttpServletResponse gameResponse = mvc.perform(get(gameLink.expand().getHref()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Link turnsLink = getDiscovererFor(gameResponse).findLinkWithRel(TURNS_REL, gameResponse.getContentAsString()).orElseThrow(AssertionError::new);

        mvc.perform(get(turnsLink.expand().getHref()).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent(IanaLinkRelations.SELF.value()))
                .andExpect(jsonPath("$.page.size", is(1)))
                .andExpect(jsonPath("$.page.totalElements", is(2)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$._links.first").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.next").exists())
                .andExpect(jsonPath("$._links.last").exists())
                .andReturn().getResponse();

    }
}
