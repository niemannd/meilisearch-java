package integrationtests;

import com.github.niemannd.meilisearch.MeiliClient;
import com.github.niemannd.meilisearch.api.documents.DocumentService;
import com.github.niemannd.meilisearch.api.documents.Update;
import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.config.ConfigurationBuilder;
import com.github.niemannd.meilisearch.http.ApacheHttpClient;
import com.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import com.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class MeiliClientIntegrationTest {
    private static final Logger log = getLogger(ApacheHttpClient.class);
    private final String testIndexName = UUID.randomUUID().toString();
    private List<Movie> data;

    private final Configuration config = new ConfigurationBuilder()
            .setUrl("http://localhost:7700")
            .setKey(() -> "masterKey")
            .addDocumentType(testIndexName, Movie.class)
            .build();

    private final JacksonJsonProcessor processor = new JacksonJsonProcessor();

    private final MeiliClient classToTest = new MeiliClient(config, new ApacheHttpClient(config, processor), processor);

    private void setTestData() throws IOException {
        String fileName = "movies.json";

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        }
        File file = new File(resource.getFile());
        data = processor.deserialize(new BufferedReader(new InputStreamReader(new FileInputStream(file))).lines().collect(Collectors.joining()), List.class, Movie.class);
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        if (data == null) {
            setTestData();
        }

        classToTest.index().createIndex(testIndexName);

        DocumentService<Movie> movieService = classToTest.documentServiceForIndex(testIndexName);
        Update update = movieService.addDocument(data);
        boolean updateFinished;
        do {
            updateFinished = "processed".equalsIgnoreCase(movieService.getUpdate(update.getUpdateId()).getStatus());
            Thread.sleep(500);
        } while (!updateFinished);
    }

    @AfterEach
    void tearDown() {
        classToTest.index().deleteIndex(testIndexName);
    }

    @Test
    void basicSearch() {
        DocumentService<Movie> movieService = classToTest.documentServiceForIndex(testIndexName);
        Movie movie = movieService.getDocument("450465");

        assertNotNull(movie);
        assertEquals("Glass", movie.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w1280/svIDTNUoajS8dLEo7EosxvyAsgJ.jpg", movie.getPoster());
        assertEquals("In a series of escalating encounters, security guard David Dunn uses his supernatural abilities to track Kevin Wendell Crumb, a disturbed man who has twenty-four personalities. Meanwhile, the shadowy presence of Elijah Price emerges as an orchestrator who holds secrets critical to both men.", movie.getOverview());
        assertEquals("1547596800", movie.getReleaseDate());
        assertNotNull(movie.getGenre());
        assertEquals(1, movie.getGenre().size());
        assertEquals("Documentary", movie.getGenre().get(0));
    }
}
