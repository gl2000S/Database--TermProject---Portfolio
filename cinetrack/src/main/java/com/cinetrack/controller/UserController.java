package com.cinetrack.controller;

import com.cinetrack.model.User;
import com.cinetrack.model.UserProfileStats;
import com.cinetrack.model.UserReview;
import com.cinetrack.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showOwnProfile(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        return "redirect:/users/" + userId;
    }

    @GetMapping("/users/{id}")
    public String showUserProfile(@PathVariable int id, Model model, HttpSession session) {
        User profileUser = userService.getUserById(id);

        if (profileUser == null) {
            model.addAttribute("error", "User not found.");
            return "user-profile";
        }

        UserProfileStats stats = userService.getUserStats(id);
        List<UserReview> recentReviews = userService.getRecentReviewsByUser(id);

        Object loggedInUserId = session.getAttribute("userId");

        boolean isOwnProfile = false;
        if (loggedInUserId instanceof Integer) {
            isOwnProfile = ((Integer) loggedInUserId) == id;
        }

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("stats", stats);
        model.addAttribute("recentReviews", recentReviews);
        model.addAttribute("isOwnProfile", isOwnProfile);
        model.addAttribute("loggedInUsername", session.getAttribute("username"));

        return "user-profile";
    }
}
