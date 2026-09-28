package com.cinetrack.controller;

import com.cinetrack.model.WatchlistEntry;
import com.cinetrack.service.WatchlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping("/watchlist")
    public String getWatchlist(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<WatchlistEntry> entries = watchlistService.getUserWatchlist(userId);

        model.addAttribute("watched", watchlistService.getByStatus(userId, "WATCHED"));
        model.addAttribute("wantToWatch", watchlistService.getByStatus(userId, "WANT_TO_WATCH"));
        model.addAttribute("dropped", watchlistService.getByStatus(userId, "DROPPED"));
        model.addAttribute("loggedInUsername", session.getAttribute("username"));
        model.addAttribute("watchedCount", watchlistService.getCountByStatus(userId, "WATCHED"));
        model.addAttribute("wantToWatchCount", watchlistService.getCountByStatus(userId, "WANT_TO_WATCH"));
        model.addAttribute("droppedCount", watchlistService.getCountByStatus(userId, "DROPPED"));

        return "watchlist";
    }

    @PostMapping("/watchlist/update")
    public String updateWatchlistStatus(@RequestParam int movieId, @RequestParam String status, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        watchlistService.updateWatchlistStatus(userId, movieId, status);

        return "redirect:/watchlist";
    }

    @PostMapping("/watchlist/delete")
    public String deleteWatchlistEntry(@RequestParam int movieId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        watchlistService.deleteWatchlistEntry(userId, movieId);

        return "redirect:/watchlist";
    }

    @PostMapping("/watchlist/add")
    public String addToWatchlist(@RequestParam int movieId, @RequestParam String status, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        watchlistService.addToWatchlist(userId, movieId, status);

        return "redirect:/movies/" + movieId;
    }    
}