# MeiliSearch Java

![GitHub](https://img.shields.io/github/license/niemannd/meilisearch-java) ![Version](https://img.shields.io/badge/version-alpha-critical)  

**MeiliSearch Java** is a client for **MeiliSearch** written in Java. **MeiliSearch** is a 
powerful, fast, open-source, easy to use and deploy search engine. Both searching and indexing 
are highly customizable. Features such as typo-tolerance, filters, and synonyms are provided out-of-the-box.



## Installation

Until i decide this project is stable enough for maven-central, please use jitpack.io.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.github.niemannd</groupId>
        <artifactId>meilisearch-java</artifactId>
        <version>master-SNAPSHOT</version>
    </dependency>
</dependencies>
```
## 🚀 Getting started

```java
public final class Example {
  
  public static void main(String args[]) throws Exception {
    Configuration config = new ConfigurationBuilder()
                .setUrl("http://localhost:7700")
                .addDocumentType("movies", Movie.class)
                .build();

    JacksonJsonProcessor processor = new JacksonJsonProcessor();
    ApacheHttpClient httpClient = new ApacheHttpClient(config, processor);
    MeiliClient meiliClient = new MeiliClient(config, httpClient, processor);

    
    meiliClient.index().createIndex("movies");
    
    DocumentService<Movie> movieService = classToTest.documentServiceForIndex(testIndexName);
    Update update = movieService.addDocument("[{\\\"id\\\":287947,\\\"title\\\":\\\"Shazam\\\",\\\"poster\\\":\\\"https://image.tmdb.org/t/p/w1280/xnopI5Xtky18MPhK40cZAGAOVeV.jpg\\\",\\\"overview\\\":\\\"Shazam\\\",\\\"release_date\\\":\\\"2019-03-23\\\"}]");
 
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
    private String id;
  }
}
```