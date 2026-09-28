package com.cinetrack.controller;

import com.cinetrack.dao.ActorDAO;
import com.cinetrack.model.Actor;
import com.cinetrack.model.ActorFilmEntry;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ActorController {

    private final ActorDAO actorDAO;

    public ActorController(ActorDAO actorDAO) {
        this.actorDAO = actorDAO;
    }

    @GetMapping("/actors/{id}")
    public String showActorProfile(@PathVariable int id, Model model, HttpSession session) {
        Actor actor = actorDAO.getActorById(id);

        if (actor == null) {
            model.addAttribute("error", "Actor not found.");
            return "actor";
        }

        List<ActorFilmEntry> filmography = actorDAO.getFilmography(id);

        model.addAttribute("actor", actor);
        model.addAttribute("filmography", filmography);
        model.addAttribute("loggedInUsername", session.getAttribute("username"));

        return "actor";
    }
}
