package io.github.niemannd.meilisearch.api.index;

public class Index {
    private String uid;
    private String name;
    private String primaryKey;
    private String createdAt;
    private String updatedAt;

    public Index() {
    }

    public Index(String uid) {
        this(null, uid, null, null, null);
    }

    public Index(String uid, String primaryKey) {
        this(null, uid, primaryKey, null, null);
    }

    public Index(String name, String uid, String primaryKey, String createdAt, String updatedAt) {
        this.name = name;
        this.uid = uid;
        this.primaryKey = primaryKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getUid() {
        return uid;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }
}
