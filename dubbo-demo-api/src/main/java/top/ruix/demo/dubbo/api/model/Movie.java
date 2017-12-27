package top.ruix.demo.dubbo.api.model;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Serializable {

    private String movieName;
    private String movieDescribe;
    private Date releaseDate;

    public Movie(String movieName, String movieDescribe, Date releaseDate) {
        this.movieName = movieName;
        this.movieDescribe = movieDescribe;
        this.releaseDate = releaseDate;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescribe() {
        return movieDescribe;
    }

    public void setMovieDescribe(String movieDescribe) {
        this.movieDescribe = movieDescribe;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieName='" + movieName + '\'' +
                ", movieDescribe='" + movieDescribe + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
