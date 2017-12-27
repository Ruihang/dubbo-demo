package top.ruix.demo.dubbo.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.ruix.demo.dubbo.api.model.Movie;
import top.ruix.demo.dubbo.api.service.IMovieService;

import java.util.List;

public class Consumer {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-consumer.xml");
        IMovieService movieService = context.getBean(IMovieService.class);
        List<Movie> all = movieService.getAll();
        System.out.println(all);
    }

}
