package io.github.niemannd.meilisearch.api.documents;

import java.util.ArrayList;
import java.util.List;

public class SearchRequest {
    private String q;
    private int offset;
    private int limit;
    private String filters;
    private List<String> attributesToRetrieve;
    private List<String> attributesToCrop;
    private int cropLength;
    private List<String> attributesToHighlight;
    private boolean matches;

    public SearchRequest(String q) {
        this(q, 0);
    }

    public SearchRequest(String q, int offset) {
        this(q, offset, 20);
    }

    public SearchRequest(String q, int offset, int limit) {
        this(q, offset, limit, "*");
    }

    public SearchRequest(String q, int offset, int limit, String attributesToRetrieve) {
        this(q, offset, limit, attributesToRetrieve, null, 200, null, null, false);
    }

    public SearchRequest(String q,
                         int offset,
                         int limit,
                         String attributesToRetrieve,
                         List<String> attributesToCrop,
                         int cropLength,
                         List<String> attributesToHighlight,
                         String filters,
                         boolean matches) {
        this.q = q;
        this.offset = offset;
        this.limit = limit;
        this.attributesToRetrieve = new ArrayList<String>() {{
            add(attributesToRetrieve);
        }};
        this.attributesToCrop = attributesToCrop;
        this.cropLength = cropLength;
        this.attributesToHighlight = attributesToHighlight;
        this.filters = filters;
        this.matches = matches;
    }

    public String getQ() {
        return q;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> getAttributesToRetrieve() {
        return attributesToRetrieve;
    }

    public List<String> getAttributesToCrop() {
        return attributesToCrop;
    }

    public int getCropLength() {
        return cropLength;
    }

    public List<String> getAttributesToHighlight() {
        return attributesToHighlight;
    }

    public String getFilters() {
        return filters;
    }

    public boolean isMatches() {
        return matches;
    }
}
