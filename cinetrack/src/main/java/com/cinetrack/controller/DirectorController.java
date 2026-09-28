package com.cinetrack.controller;

import com.cinetrack.dao.DirectorDAO;
import com.cinetrack.model.Director;
import com.cinetrack.model.DirectorFilmEntry;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DirectorController {

    private final DirectorDAO directorDAO;

    public DirectorController(DirectorDAO directorDAO) {
        this.directorDAO = directorDAO;
    }

    @GetMapping("/directors/{id}")
    public String showDirectorProfile(@PathVariable int id, Model model, HttpSession session) {
        Director director = directorDAO.getDirectorById(id);

        if (director == null) {
            model.addAttribute("error", "Director not found.");
            return "director";
        }

        List<DirectorFilmEntry> filmography = directorDAO.getFilmography(id);

        model.addAttribute("director", director);
        model.addAttribute("filmography", filmography);
        model.addAttribute("loggedInUsername", session.getAttribute("username"));

        return "director";
    }
}
