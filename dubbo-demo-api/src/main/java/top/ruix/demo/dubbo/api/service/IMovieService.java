package top.ruix.demo.dubbo.api.service;

import top.ruix.demo.dubbo.api.model.Movie;

import java.util.List;

public interface IMovieService {

    List<Movie> getAll();

}
