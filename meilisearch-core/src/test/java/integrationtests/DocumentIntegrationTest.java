package integrationtests;

import com.github.niemannd.meilisearch.api.MeiliException;
import com.github.niemannd.meilisearch.api.documents.DocumentService;
import com.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DocumentIntegrationTest extends AbstractIT {

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
    void basicUseCase() {
        key.setKey("test");
        DocumentService<Movie> movies = client.documentServiceForIndex(testIndexName);
        assertThrows(MeiliException.class,() -> movies.getDocument("450465"));
    }
}
