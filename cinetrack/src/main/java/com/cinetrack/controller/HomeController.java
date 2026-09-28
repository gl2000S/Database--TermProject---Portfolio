package com.cinetrack.controller;

import com.cinetrack.dao.HomeDAO;
import com.cinetrack.model.Movie;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private static final int PAGE_SIZE = 20;

    private final HomeDAO homeDAO;

    public HomeController(HomeDAO homeDAO) {
        this.homeDAO = homeDAO;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(value = "q", required = false) String query,
                       HttpSession session, Model model) {

        String trimmed = (query != null && !query.isBlank()) ? query.trim() : null;

        List<Movie> movies = homeDAO.getMoviesPage(trimmed, 0, PAGE_SIZE);
        for (Movie m : movies) {
            m.setAvgRatingFormatted(m.getAvgRating() != null
                    ? String.format("%.1f", m.getAvgRating()) : null);
        }

        var directors = homeDAO.getDirectorsPage(0, PAGE_SIZE);
        var actors    = homeDAO.getActorsPage(0, PAGE_SIZE);

        var recentActivity = homeDAO.getRecentActivity(10);

        if (trimmed != null) model.addAttribute("searchQuery", trimmed);

        model.addAttribute("movies",           movies);
        model.addAttribute("directors",        directors);
        model.addAttribute("actors",           actors);
        model.addAttribute("movieCount",       movies.size());
        model.addAttribute("directorCount",    directors.size());
        model.addAttribute("actorCount",       actors.size());
        model.addAttribute("hasMoreMovies",    movies.size()    == PAGE_SIZE);
        model.addAttribute("hasMoreDirectors", directors.size() == PAGE_SIZE);
        model.addAttribute("hasMoreActors",    actors.size()    == PAGE_SIZE);
        model.addAttribute("recentActivity",    recentActivity);
        model.addAttribute("loggedInUsername", session.getAttribute("username"));

        return "home";
    }
}
