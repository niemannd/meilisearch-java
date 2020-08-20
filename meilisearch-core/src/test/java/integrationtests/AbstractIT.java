package integrationtests;

import io.github.niemannd.meilisearch.MeiliClient;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.config.ConfigurationBuilder;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.utils.Movie;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractIT {
    protected final String testIndexName = UUID.randomUUID().toString();
    protected final KeySupplier key;
    protected List<Movie> data;

    protected final JacksonJsonProcessor processor = new JacksonJsonProcessor();

    protected final MeiliClient client;

    public AbstractIT() {
        String meiliUrl = System.getProperty("MEILI_HTTP_ADDR", "http://127.0.0.1:7700");
        String meiliKey = System.getProperty("MEILI_MASTER_KEY", "masterKey");

        key = new KeySupplier(meiliKey);
        Configuration config = new ConfigurationBuilder()
                .setUrl(meiliUrl)
                .setKey(key)
                .addDocumentType(testIndexName, Movie.class)
                .build();

        client = new MeiliClient(config, new ApacheHttpClient(config, processor), processor);
    }

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

    void setUp() throws IOException, InterruptedException {
        if (data == null) {
            setTestData();
        }

        setMasterKey();
        client.index().createIndex(testIndexName);

        DocumentService<Movie> movieService = client.documentServiceForIndex(testIndexName);
        Update update = movieService.addDocument(data);
        waitForUpdate(update);
    }

    void tearDown() {
        setMasterKey();
        client.index().deleteIndex(testIndexName);
    }

    private void setMasterKey() {
        String meiliKey = System.getProperty("MEILI_MASTER_KEY", "masterKey");
        key.setKey(meiliKey);
    }

    protected void waitForUpdate(Update update) {
        boolean updateFinished;
        do {
            updateFinished = "processed".equalsIgnoreCase(client.documentServiceForIndex(testIndexName).getUpdate(update.getUpdateId()).getStatus());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!updateFinished);
    }
}
