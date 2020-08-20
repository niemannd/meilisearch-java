package io.github.niemannd.meilisearch.api.documents;

import java.util.List;

public class SearchRequestBuilder {
    private String q;
    private int offset;
    private int limit;
    private String filters;
    private List<String> attributesToRetrieve;
    private List<String> attributesToCrop;
    private int cropLength;
    private List<String> attributesToHighlight;
    private boolean matches;

    public SearchRequestBuilder setQ(String q) {
        this.q = q;
        return this;
    }

    public SearchRequestBuilder setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public SearchRequestBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public SearchRequestBuilder setFilters(String filters) {
        this.filters = filters;
        return this;
    }

    public SearchRequestBuilder setAttributesToRetrieve(List<String> attributesToRetrieve) {
        this.attributesToRetrieve = attributesToRetrieve;
        return this;
    }

    public SearchRequestBuilder setAttributesToCrop(List<String> attributesToCrop) {
        this.attributesToCrop = attributesToCrop;
        return this;
    }

    public SearchRequestBuilder setCropLength(int cropLength) {
        this.cropLength = cropLength;
        return this;
    }

    public SearchRequestBuilder setAttributesToHighlight(List<String> attributesToHighlight) {
        this.attributesToHighlight = attributesToHighlight;
        return this;
    }

    public SearchRequestBuilder setMatches(boolean matches) {
        this.matches = matches;
        return this;
    }

    public SearchRequest build() {
        return new SearchRequest(
                q,
                offset,
                limit,
                attributesToRetrieve,
                attributesToCrop,
                cropLength,
                attributesToHighlight,
                filters,
                matches
        );
    }
}
