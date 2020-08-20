package io.github.niemannd.meilisearch.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class Movie {
    private float id;
    private String title;
    private String poster;
    private String overview;
    @JsonProperty("release_date")
    private String releaseDate;
    private List<String> genre;

    public Movie() {
    }

    public Movie(float id, String title, String poster, String overview, String releaseDate, String... genre) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genre = Arrays.asList(genre);
    }

    public Movie(float id, String title, String poster, String overview, String releaseDate, List<String> genre) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }
}
