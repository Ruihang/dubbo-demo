package top.ruix.demo.dubbo.provider.service.impl;

import top.ruix.demo.dubbo.api.model.Movie;
import top.ruix.demo.dubbo.api.service.IMovieService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieServiceImpl implements IMovieService {

    @Override
    public List<Movie> getAll() {
        List<Movie> movies = new ArrayList<Movie>();
        movies.add(new Movie("大话西游1","大话西游1的描述", new Date()));
        movies.add(new Movie("大话西游2","大话西游2的描述", new Date()));
        movies.add(new Movie("大话西游3","大话西游3的描述", new Date()));
        return movies;
    }
}
