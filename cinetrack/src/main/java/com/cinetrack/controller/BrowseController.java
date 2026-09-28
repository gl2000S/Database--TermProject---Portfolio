package com.cinetrack.controller;

import com.cinetrack.dao.HomeDAO;
import com.cinetrack.model.Movie;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/browse")
public class BrowseController {

    private static final int MAX_LIMIT = 40;

    private final HomeDAO homeDAO;

    public BrowseController(HomeDAO homeDAO) {
        this.homeDAO = homeDAO;
    }

    record MovieCard(int movieId, String title, Integer releaseYear,
                     String posterUrl, String avgRatingFormatted) {}

    record DirectorCard(int directorId, String name, String photoUrl) {}

    record ActorCard(int actorId, String name, String photoUrl) {}

    @GetMapping("/movies")
    public List<MovieCard> movies(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

        limit = Math.min(limit, MAX_LIMIT);
        String q = (query != null && !query.isBlank()) ? query.trim() : null;

        List<Movie> movies = homeDAO.getMoviesPage(q, offset, limit);
        return movies.stream().map(m -> new MovieCard(
                m.getMovieId(),
                m.getTitle(),
                m.getReleaseYear(),
                m.getPosterUrl(),
                m.getAvgRating() != null ? String.format("%.1f", m.getAvgRating()) : null
        )).collect(Collectors.toList());
    }

    @GetMapping("/directors")
    public List<DirectorCard> directors(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

        limit = Math.min(limit, MAX_LIMIT);
        return homeDAO.getDirectorsPage(offset, limit).stream()
                .map(d -> new DirectorCard(d.getDirectorId(), d.getName(), d.getPhotoUrl()))
                .collect(Collectors.toList());
    }

    @GetMapping("/actors")
    public List<ActorCard> actors(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

        limit = Math.min(limit, MAX_LIMIT);
        return homeDAO.getActorsPage(offset, limit).stream()
                .map(a -> new ActorCard(a.getActorId(), a.getName(), a.getPhotoUrl()))
                .collect(Collectors.toList());
    }
}
