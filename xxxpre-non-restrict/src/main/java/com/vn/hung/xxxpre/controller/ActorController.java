package com.vn.hung.xxxpre.controller;

import com.vn.hung.xxxpre.entity.Actor;
import com.vn.hung.xxxpre.service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actors")
public class ActorController {

    @Autowired
    private ActorService actorService;

    /**
     * Lists all actors available in the database.
     * GET /api/v1/actors
     */
    @GetMapping
    public ResponseEntity<List<Actor>> listAllActors() {
        List<Actor> actors = actorService.listAllActors();
        return ResponseEntity.ok(actors);
    }
}