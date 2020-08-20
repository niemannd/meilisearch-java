package io.github.niemannd.meilisearch.config;

public class DocumentTypeNotFoundException extends RuntimeException {
    public DocumentTypeNotFoundException(String index) {
        super("no documentType for index '" + index + "' could be found");
    }
}
