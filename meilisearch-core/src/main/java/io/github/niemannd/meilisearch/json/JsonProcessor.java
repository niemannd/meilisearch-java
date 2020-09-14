package io.github.niemannd.meilisearch.json;

import io.github.niemannd.meilisearch.api.MeiliException;

public interface JsonProcessor {
    /**
     *
     * @param o the Object to serialize
     * @return the serialized Object {@code o}
     * @throws MeiliException wrapped exceptions of the used json library
     */
    String serialize(Object o) throws MeiliException;

    /**
     * e.g. deserialize(somesthing, List.class, String.class) will try to deserialize "somestring" into a List<String>
     *
     * @param o Object to deserialize, most of the time this is a string
     * @param targetClass return type
     * @param parameters in case the return type is a generic class, this is a list of types to use with that generic.
     * @return the deserialized object
     * @throws MeiliException wrapped exceptions of the used json library
     */
    <T> T deserialize(Object o, Class<?> targetClass, Class<?>... parameters) throws MeiliException;
}
