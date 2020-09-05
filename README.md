# MeiliSearch Java

![GitHub](https://img.shields.io/github/license/niemannd/meilisearch-java) 
![maven test](https://github.com/niemannd/meilisearch-java/workflows/maven%20test/badge.svg) 
[![codecov](https://codecov.io/gh/niemannd/meilisearch-java/branch/master/graph/badge.svg)](https://codecov.io/gh/niemannd/meilisearch-java)  
[![](https://jitpack.io/v/niemannd/meilisearch-java.svg)](https://jitpack.io/#niemannd/meilisearch-java)

| Important!: this project is still WIP and not recommended for production |
| --- |

**MeiliSearch Java** is a client for **MeiliSearch** written in Java. **MeiliSearch** is a 
powerful, fast, open-source, easy to use and deploy search engine. Both searching and indexing 
are highly customizable. Features such as typo-tolerance, filters, and synonyms are provided out-of-the-box.



## Installation

Until i decide this project is stable enough for maven-central, please use jitpack.io.

Step 1. Add the JitPack repository to your build file
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
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
    MeiliClient meiliClient = new MeiliClient(config, httpClient, processor);

    
    meiliClient.index().createIndex("movies");
    
    // add documents directly via string
    DocumentService<Movie> movieService = classToTest.documentService(Movie.class);
    Update update = movieService.addDocument("[{\"id\":287947,\"title\":\"Shazam\",\"poster\":\"https://image.tmdb.org/t/p/w1280/xnopI5Xtky18MPhK40cZAGAOVeV.jpg\",\"overview\":\"Shazam\",\"release_date\":\"2019-03-23\"}]");
 
    //or via document list
    List<Movie> movieList = new ArrayList();
    movieList.add(new Movie(...));
    Update update = movieService.addDocument(movieList);

    // wait for the update to finish
    boolean updateFinished = false;
    do {
        updateFinished = "processed".equalsIgnoreCase(movieService.getUpdate(update.getUpdateId()).getStatus());
        Thread.sleep(500);
    } while (!updateFinished);
    
    SearchResult<Movie> result = movieService.search("Shazam");
    
  }
  
  public static class Movie {
    private int id;
    private String title;
  }
}
```
## Customizing the HttpClient

This client uses a small abstraction layer to decouple the used http client. To use your own http client create an implementation of the `HttpClient` interface.
Alternatively you can use the provided implementation for [Apache HttpClient 5.0](https://hc.apache.org/httpcomponents-client-5.0.x/index.html).
If you choose to use the Apache HttpClient implementation, please add Apache HttpClient as a dependency to your project.

## Customizing the HttpClient

This client uses a small abstraction layer to decouple the used json library. To use your own json library create an implementation of the `JsonProcessor` interface.
Alternatively you can use one of the provided implementations:
* `JacksonJsonProcessor` for Jackson Databind  
* `GsonJsonProcessor` for Google Gson
