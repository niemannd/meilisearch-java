package io.github.niemannd.meilisearch.api.index;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.niemannd.meilisearch.GenericServiceTemplate;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.http.response.BasicHttpResponse;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SettingsServiceTest {

    private final HttpClient<String> client = mock(ApacheHttpClient.class);
    private final JsonProcessor processor = new JacksonJsonProcessor(new ObjectMapper());
    private final SettingsService classToTest = new SettingsService(new GenericServiceTemplate(client, processor));

    @Test
    void getSettings() {
        when(client.get(any(), any())).thenReturn(new BasicHttpResponse(null, 200, "{\"rankingRules\":[\"typo\",\"words\",\"proximity\",\"attribute\",\"wordsPosition\",\"exactness\",\"desc(release_date)\"],\"attributesForFaceting\":[\"genre\"],\"distinctAttribute\":null,\"searchableAttributes\":[\"title\",\"description\",\"uid\"],\"displayedAttributes\":[\"title\",\"description\",\"release_date\",\"rank\",\"poster\"],\"stopWords\":null,\"synonyms\":{\"wolverine\":[\"xmen\",\"logan\"],\"logan\":[\"wolverine\",\"xmen\"]}}"));
        Settings test = classToTest.getSettings("test");
        assertThat(test, notNullValue());
        assertThat(test.getSynonyms(), notNullValue());
        assertThat(test.getDistinctAttribute(), nullValue());
        assertThat(test.getStopWords(), nullValue());
        assertThat(test.getSynonyms().keySet(), containsInAnyOrder("wolverine", "logan"));
        assertThat(test.getSynonyms().get("wolverine"), containsInAnyOrder("xmen", "logan"));
        assertThat(test.getSynonyms().get("logan"), containsInAnyOrder("wolverine", "xmen"));
        assertThat(test.getDisplayedAttributes(), containsInAnyOrder("title", "description", "release_date", "rank", "poster"));
        assertThat(test.getRankingRules(), containsInRelativeOrder("typo", "words", "proximity", "attribute", "wordsPosition", "exactness", "desc(release_date)"));
        assertThat(test.getAttributesForFaceting(), containsInAnyOrder("genre"));
        assertThat(test.getSearchableAttributes(), containsInAnyOrder("title", "description", "uid"));
    }

    @Test
    void updateSettings() throws JsonProcessingException {
        when(client.post(any(), any())).thenReturn(new BasicHttpResponse(null, 200, "{\"updateId\": 1}"));
        Settings settings = new ObjectMapper().readValue("{\"rankingRules\":[\"typo\",\"words\",\"proximity\",\"attribute\",\"wordsPosition\",\"exactness\",\"desc(release_date)\"],\"attributesForFaceting\":[\"genre\"],\"distinctAttribute\":null,\"searchableAttributes\":[\"title\",\"description\",\"uid\"],\"displayedAttributes\":[\"title\",\"description\",\"release_date\",\"rank\",\"poster\"],\"stopWords\":null,\"synonyms\":{\"wolverine\":[\"xmen\",\"logan\"],\"logan\":[\"wolverine\",\"xmen\"]}}", Settings.class);
        Update test = classToTest.updateSettings("test", settings);
        assertThat(test, notNullValue());
        assertThat(test.getUpdateId(), is(1));
    }

    @Test
    void resetSettings() {
        when(client.delete(any())).thenReturn(new BasicHttpResponse(null, 200, "{\"updateId\": 1}"));
        Update test = classToTest.resetSettings("test");
        assertThat(test, notNullValue());
        assertThat(test.getUpdateId(), is(1));
    }
}