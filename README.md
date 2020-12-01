# MeiliSearch Java

![GitHub](https://img.shields.io/github/license/niemannd/meilisearch-java)
![maven test](https://github.com/niemannd/meilisearch-java/workflows/maven%20test/badge.svg)
[![codecov](https://codecov.io/gh/niemannd/meilisearch-java/branch/master/graph/badge.svg)](https://codecov.io/gh/niemannd/meilisearch-java)  [![](https://jitpack.io/v/niemannd/meilisearch-java.svg)](https://jitpack.io/#niemannd/meilisearch-java)   


| Important!: this project is still WIP and not recommended for production |
| --- |

**MeiliSearch Java** is a client for **MeiliSearch** written in Java. **MeiliSearch** is a 
powerful, fast, open-source, easy to use and deploy search engine. Both searching and indexing 
are highly customizable. Features such as typo-tolerance, filters, and synonyms are provided out-of-the-box.



## Installation

Until i decide this project is stable enough for maven-central, please use jitpack.io.

Step 1. Add the JitPack repository to your build file
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
Step 2. Add the dependency
```xml
<dependency>
    <groupId>com.github.niemannd.meilisearch-java</groupId>
    <artifactId>meilisearch-core</artifactId>
    <version>0.1.0</version>
</dependency>
```
## Getting started

```java
public final class Example {
  
  public static void main(String args[]) throws Exception {
        Configuration config = new ConfigurationBuilder()
                .setUrl("http://localhost:7700")
                .setKey(() -> "masterKey")
                .addDocumentType("movies", Movie.class)
                .build();

        JacksonJsonProcessor processor = new JacksonJsonProcessor();
        ApacheHttpClient httpClient = new ApacheHttpClient(config, processor);
        MeiliClient client = new MeiliClient(config, httpClient, processor);
        client.indexes().createIndex("movies");

        // add documents directly via string
        DocumentService<Movie> movieService = client.documents(Movie.class);
        Update update = movieService.addDocument("[{\"id\":287947,\"title\":\"Shazam\"}]");

        //or via document list
        List<Movie> movieList = new ArrayList();
        movieList.add(new Movie(1, "Shazam"));
        update = movieService.addDocument(movieList);

        // wait for the update to finish
        boolean updateFinished = false;
        do {
            updateFinished = "processed".equalsIgnoreCase(movieService.getUpdate(update.getUpdateId()).getStatus());
            Thread.sleep(500);
        } while (!updateFinished);

        SearchResponse<Movie> result = movieService.search("Shazam");
  }
  
  public static class Movie {
    private int id;
    private String title;

    public Movie(float id, String title) {
        this.id = id;
        this.title = title;
    }
  }
}
```
## Customizing the HttpClient

This client uses a small abstraction layer to decouple the used http client. To use your own http client create an implementation of the `HttpClient` interface.
Alternatively you can use the provided implementation for [Apache HttpClient 5.0](https://hc.apache.org/httpcomponents-client-5.0.x/index.html).
If you choose to use the Apache HttpClient implementation, please add Apache HttpClient as a dependency to your project.

## Customizing the JsonProcessor

This client uses a small abstraction layer to decouple the used json library. To use your own json library create an implementation of the `JsonProcessor` interface.
Alternatively you can use one of the provided implementations:
* `JacksonJsonProcessor` for Jackson Databind  
* `GsonJsonProcessor` for Google Gson
