package integrationtests;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.documents.SearchRequest;
import io.github.niemannd.meilisearch.api.documents.SearchRequestBuilder;
import io.github.niemannd.meilisearch.api.documents.SearchResponse;
import io.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SearchIntegrationTest extends AbstractIT {

    @Override
    @BeforeEach
    void setUp() throws java.io.IOException, InterruptedException {
        super.setUp();
    }

    @Override
    @AfterEach
    void tearDown() {
        super.tearDown();
    }

    @Test
    void basicSearch() {
        DocumentService<Movie> movieService = client.documentServiceForIndex(testIndexName);
        SearchResponse<Movie> movieResult = movieService.search("450465");

        assertNotNull(movieResult);
        assertNotNull(movieResult.getHits());
        assertEquals(1, movieResult.getHits().size());

        Movie movie = movieResult.getHits().get(0);

        assertNotNull(movie);
        assertEquals("Glass", movie.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w1280/svIDTNUoajS8dLEo7EosxvyAsgJ.jpg", movie.getPoster());
        assertEquals("In a series of escalating encounters, security guard David Dunn uses his supernatural abilities to track Kevin Wendell Crumb, a disturbed man who has twenty-four personalities. Meanwhile, the shadowy presence of Elijah Price emerges as an orchestrator who holds secrets critical to both men.", movie.getOverview());
        assertEquals("1547596800", movie.getReleaseDate());
        assertNotNull(movie.getGenre());
        assertEquals(1, movie.getGenre().size());
        assertEquals("Documentary", movie.getGenre().get(0));
    }

    @Test
    void complexSearch() {
        DocumentService<Movie> movieService = client.documentServiceForIndex(testIndexName);
        SearchResponse<Movie> movieResult = movieService.search(new SearchRequest("450465", 0, 10, Collections.singletonList("title")));

        assertNotNull(movieResult);
        assertNotNull(movieResult.getHits());
        assertEquals(1, movieResult.getHits().size());

        Movie movie = movieResult.getHits().get(0);

        assertNotNull(movie);
        assertEquals("Glass", movie.getTitle());
        assertNull(movie.getPoster());
        assertNull(movie.getGenre());
        assertNull(movie.getReleaseDate());
        assertNull(movie.getOverview());
    }

    @Test
    void withoutPermission() {
        DocumentService<Movie> movieService = client.documentServiceForIndex(testIndexName);

        key.setKey(null);
        assertThrows(MeiliException.class, () -> movieService.search(new SearchRequest("450465", 0, 10, Collections.singletonList("title"))));
        assertThrows(MeiliException.class, () -> movieService.search("450465"));
        key.setKey("8dcbb482663333d0280fa9fedf0e0c16d52185cb67db494ce4cd34da32ce2092");
        assertDoesNotThrow(() -> movieService.search(new SearchRequest("450465", 0, 10, Collections.singletonList("title"))));
        assertDoesNotThrow(() -> movieService.search("450465"));
        key.setKey("3b3bf839485f90453acc6159ba18fbed673ca88523093def11a9b4f4320e44a5");
        assertDoesNotThrow(() -> movieService.search(new SearchRequest("450465", 0, 10, Collections.singletonList("title"))));
        assertDoesNotThrow(() -> movieService.search("450465"));

    }

    @Test
    void withFilter() {
        DocumentService<Movie> movieService = client.documentServiceForIndex(testIndexName);

        SearchResponse<Movie> search = movieService.search(
                new SearchRequestBuilder()
                        .setQ("S")
                        .setAttributesToRetrieve(Arrays.asList("id", "release_date"))
                        .build()
        );
        assertEquals(45, search.getNbHits());
        assertNotNull(search.getHits());
        assertEquals(10, search.getHits().size());
        assertEquals(287947, search.getHits().get(0).getId());
        assertNull(search.getHits().get(0).getTitle());
        assertNull(search.getHits().get(0).getPoster());
        assertNull(search.getHits().get(0).getOverview());
        assertEquals("1553299200", search.getHits().get(0).getReleaseDate());
        assertNull(search.getHits().get(0).getGenre());
    }
}
