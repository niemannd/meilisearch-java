package com.github.niemannd.meilisearch.api.documents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.config.ConfigurationBuilder;
import com.github.niemannd.meilisearch.http.HttpClient;
import com.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import com.github.niemannd.meilisearch.json.JsonProcessor;
import com.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentServiceTest {

    private HttpClient client = mock(HttpClient.class);
    private Configuration config = new ConfigurationBuilder().addDocumentType("movies", Movie.class).setUrl("https://localhost").build();
    private JsonProcessor processor = new JacksonJsonProcessor(new ObjectMapper());
    private final DocumentService<Movie> classToTest = new DocumentService<Movie>("movies", client, config, processor);

    @Test
    void getDocument() {
        when(client.get(any(String.class), any())).thenReturn("{\"id\":25684,\"title\":\"American Ninja 5\",\"poster\":\"https://image.tmdb.org/t/p/w1280/iuAQVI4mvjI83wnirpD8GVNRVuY.jpg\",\"overview\":\"When a scientists daughter is kidnapped, American Ninja, attempts to find her, but this time he teams up with a youngster he has trained in the ways of the ninja.\",\"release_date\":\"1993-01-01\"}");
        Movie movie = (Movie) classToTest.getDocument("25684");
        assertNotNull(movie);
        assertEquals(25684, movie.getId());
        assertEquals("American Ninja 5", movie.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w1280/iuAQVI4mvjI83wnirpD8GVNRVuY.jpg", movie.getPoster());
        assertEquals("When a scientists daughter is kidnapped, American Ninja, attempts to find her, but this time he teams up with a youngster he has trained in the ways of the ninja.", movie.getOverview());
        assertEquals("1993-01-01", movie.getReleaseDate());
    }

    @Test
    void getDocuments() {
        when(client.get(any(String.class), any())).thenReturn("[{\"id\":25684,\"release_date\":\"1993-01-01\",\"poster\":\"https://image.tmdb.org/t/p/w1280/iuAQVI4mvjI83wnirpD8GVNRVuY.jpg\",\"title\":\"American Ninja 5\",\"overview\":\"When a scientists daughter is kidnapped, American Ninja, attempts to find her, but this time he teams up with a youngster he has trained in the ways of the ninja.\"},{\"id\":468219,\"title\":\"Dead in a Week (Or Your Money Back)\",\"release_date\":\"2018-09-12\",\"poster\":\"https://image.tmdb.org/t/p/w1280/f4ANVEuEaGy2oP5M0Y2P1dwxUNn.jpg\",\"overview\":\"William has failed to kill himself so many times that he outsources his suicide to aging assassin Leslie. But with the contract signed and death assured within a week (or his money back), William suddenly discovers reasons to live... However Leslie is under pressure from his boss to make sure the contract is completed.\"}]");
        List<Movie> movies = classToTest.getDocuments();
        assertNotNull(movies);
        assertEquals(2, movies.size());
        Movie movie = movies.get(0);
        assertEquals(25684, movie.getId());
        assertEquals("American Ninja 5", movie.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w1280/iuAQVI4mvjI83wnirpD8GVNRVuY.jpg", movie.getPoster());
        assertEquals("When a scientists daughter is kidnapped, American Ninja, attempts to find her, but this time he teams up with a youngster he has trained in the ways of the ninja.", movie.getOverview());
        assertEquals("1993-01-01", movie.getReleaseDate());
    }

    @Test
    void addAndReplaceDocument() {
        when(client.post(any(String.class), any())).thenReturn("{\"updateId\":1}");
        Update update = classToTest.replaceDocument("[{\"id\":287947,\"title\":\"Shazam\",\"poster\":\"https://image.tmdb.org/t/p/w1280/xnopI5Xtky18MPhK40cZAGAOVeV.jpg\",\"overview\":\"A boy is given the ability to become an adult superhero in times of need with a single magic word.\",\"release_date\":\"2019-03-23\"}]");
        assertNotNull(update);
        assertEquals(1, update.getUpdateId());
    }

    @Test
    void addAndUpdateDocument() {
        when(client.put(any(String.class), any(), any())).thenReturn("{\"updateId\":1}");
        Update update = classToTest.updateDocument("[{\"id\":287947,\"title\":\"Shazam\",\"poster\":\"https://image.tmdb.org/t/p/w1280/xnopI5Xtky18MPhK40cZAGAOVeV.jpg\",\"overview\":\"A boy is given the ability to become an adult superhero in times of need with a single magic word.\",\"release_date\":\"2019-03-23\"}]");
        assertNotNull(update);
        assertEquals(1, update.getUpdateId());
    }

    @Test
    void deleteDocument() {
        when(client.delete(any(String.class))).thenReturn(true);
        assertTrue(classToTest.deleteDocument("123"));
    }

    @Test
    void deleteDocuments() {
        when(client.delete(any(String.class))).thenReturn(true);
        assertTrue(classToTest.deleteDocuments());
    }

    @Test
    void search() {
        when(client.post(any(String.class), any())).thenReturn("{\"hits\":[{\"id\":\"2770\",\"title\":\"American Pie 2\",\"poster\":\"https://image.tmdb.org/t/p/w1280/q4LNgUnRfltxzp3gf1MAGiK5LhV.jpg\",\"overview\":\"The whole gang are back and as close as ever. They decide to get even closer by spending the summer together at a beach house. They decide to hold the biggest...\",\"release_date\":997405200},{\"id\":\"190859\",\"title\":\"American Sniper\",\"poster\":\"https://image.tmdb.org/t/p/w1280/svPHnYE7N5NAGO49dBmRhq0vDQ3.jpg\",\"overview\":\"U.S. Navy SEAL Chris Kyle takes his sole mission—protect his comrades—to heart and becomes one of the most lethal snipers in American history. His pinpoint accuracy not only saves countless lives but also makes him a prime...\",\"release_date\":1418256000}],\"offset\":0,\"limit\":20,\"nbHits\":1,\"exhaustiveNbHits\":false,\"processingTimeMs\":2,\"query\":\"test120232\"}");
        SearchResponse<Movie> movieSearchResponse = classToTest.search("American Pie");
        assertNotNull(movieSearchResponse);
        assertNotNull(movieSearchResponse.getHits());
        assertEquals(2, movieSearchResponse.getHits().size());
        assertEquals("American Pie 2", movieSearchResponse.getHits().get(0).getTitle());
        assertEquals(2770, movieSearchResponse.getHits().get(0).getId());
        assertEquals("https://image.tmdb.org/t/p/w1280/q4LNgUnRfltxzp3gf1MAGiK5LhV.jpg", movieSearchResponse.getHits().get(0).getPoster());
        assertEquals("997405200", movieSearchResponse.getHits().get(0).getReleaseDate());
    }

    @Test
    void testSearch() {
        when(client.post(any(String.class), any())).thenReturn("{\"hits\":[{\"id\":\"2770\",\"title\":\"American Pie 2\",\"poster\":\"https://image.tmdb.org/t/p/w1280/q4LNgUnRfltxzp3gf1MAGiK5LhV.jpg\",\"overview\":\"The whole gang are back and as close as ever. They decide to get even closer by spending the summer together at a beach house. They decide to hold the biggest...\",\"release_date\":997405200},{\"id\":\"190859\",\"title\":\"American Sniper\",\"poster\":\"https://image.tmdb.org/t/p/w1280/svPHnYE7N5NAGO49dBmRhq0vDQ3.jpg\",\"overview\":\"U.S. Navy SEAL Chris Kyle takes his sole mission—protect his comrades—to heart and becomes one of the most lethal snipers in American history. His pinpoint accuracy not only saves countless lives but also makes him a prime...\",\"release_date\":1418256000}],\"offset\":0,\"limit\":20,\"nbHits\":1,\"exhaustiveNbHits\":false,\"processingTimeMs\":2,\"query\":\"test120232\"}");
        SearchResponse<Movie> movieSearchResponse = classToTest.search(new SearchRequest("american pie"));
        assertNotNull(movieSearchResponse);
        assertNotNull(movieSearchResponse.getHits());
        assertEquals(2, movieSearchResponse.getHits().size());
        assertEquals("American Pie 2", movieSearchResponse.getHits().get(0).getTitle());
        assertEquals(2770, movieSearchResponse.getHits().get(0).getId());
        assertEquals("https://image.tmdb.org/t/p/w1280/q4LNgUnRfltxzp3gf1MAGiK5LhV.jpg", movieSearchResponse.getHits().get(0).getPoster());
        assertEquals("997405200", movieSearchResponse.getHits().get(0).getReleaseDate());

    }

    @Test
    void getUpdate() {
        when(client.get(any(String.class), any())).thenReturn("{\"status\":\"processed\",\"updateId\":1,\"type\":{\"name\":\"DocumentsAddition\",\"number\":4},\"duration\":0.076980613,\"enqueuedAt\":\"2019-12-07T21:16:09.623944Z\",\"processedAt\":\"2019-12-07T21:16:09.703509Z\"}");
        Update update = classToTest.getUpdate(1);
        assertNotNull(update);
        assertEquals("processed", update.getStatus());
        assertEquals(1, update.getUpdateId());
        assertEquals("DocumentsAddition", update.getType().getName());
        assertEquals(4, update.getType().getNumber());
        assertEquals(0.076980613, update.getDuration());
        assertEquals("2019-12-07T21:16:09.623944Z", update.getEnqueuedAt());
        assertEquals("2019-12-07T21:16:09.703509Z", update.getProcessedAt());
    }

    @Test
    void getUpdates() {
        when(client.get(any(String.class), any())).thenReturn("[{\"status\":\"processed\",\"updateId\":1,\"type\":{\"name\":\"DocumentsAddition\",\"number\":4},\"duration\":0.076980613,\"enqueuedAt\":\"2019-12-07T21:16:09.623944Z\",\"processedAt\":\"2019-12-07T21:16:09.703509Z\"}]");
        List<Update> updates = classToTest.getUpdates();
        assertNotNull(updates);
        assertEquals(1, updates.size());
        Update update = updates.get(0);
        assertEquals("processed", update.getStatus());
        assertEquals(1, update.getUpdateId());
        assertEquals("DocumentsAddition", update.getType().getName());
        assertEquals(4, update.getType().getNumber());
        assertEquals(0.076980613, update.getDuration());
        assertEquals("2019-12-07T21:16:09.623944Z", update.getEnqueuedAt());
        assertEquals("2019-12-07T21:16:09.703509Z", update.getProcessedAt());
    }

}