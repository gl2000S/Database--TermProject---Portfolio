package com.cinetrack.controller;

import com.cinetrack.dao.MovieDAO;
import com.cinetrack.model.Movie;
import com.cinetrack.model.MovieCastEntry;
import com.cinetrack.model.MovieDirectorEntry;
import com.cinetrack.model.MovieReview;
import com.cinetrack.service.WatchlistService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class MovieController {

    private final MovieDAO movieDAO;
    private final WatchlistService watchlistService;

    public MovieController(MovieDAO movieDAO, WatchlistService watchlistService) {
        this.movieDAO = movieDAO;
        this.watchlistService = watchlistService;
    }

    @GetMapping("/movies/{id}")
    public String showMovieDetail(@PathVariable int id, Model model, HttpSession session) {
        Movie movie = movieDAO.getMovieById(id);

        if (movie == null) {
            model.addAttribute("error", "Movie not found.");
            return "movie";
        }

        movie.setGenres(movieDAO.getGenres(id));
        List<MovieDirectorEntry> directors = movieDAO.getDirectors(id);
        List<MovieCastEntry> cast = movieDAO.getCast(id);
        List<MovieReview> reviews = movieDAO.getReviews(id);
        double[] stat = movieDAO.getRatingStat(id);

        movie.setAvgRatingFormatted(String.format("%.2f", stat[0]));
        movie.setReviewCount((int) stat[1]);

        model.addAttribute("movie", movie);
        model.addAttribute("movieId", id);
        model.addAttribute("directors", directors);
        model.addAttribute("cast", cast);
        model.addAttribute("reviews", reviews);
        model.addAttribute("loggedInUsername", session.getAttribute("username"));

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("canReview", true);
            model.addAttribute("isLoggedIn", true);
            MovieReview existing = movieDAO.getUserReview(userId, id);
            if (existing != null) {
                model.addAttribute("existingReview", existing);
            }
            String userMovieStatus = watchlistService.getUserMovieStatus(userId, id);
            if (userMovieStatus != null) {
                model.addAttribute("userMovieStatus", userMovieStatus);
            }
        }

        return "movie";
    }

    @PostMapping("/movies/{id}/review")
    public String submitReview(@PathVariable int id,
                               @RequestParam int rating,
                               @RequestParam(required = false, defaultValue = "") String body,
                               HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        movieDAO.upsertReview(userId, id, rating, body);
        return "redirect:/movies/" + id;
    }
}
