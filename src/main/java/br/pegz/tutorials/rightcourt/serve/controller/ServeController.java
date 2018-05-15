package br.pegz.tutorials.rightcourt.serve.controller;

import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import br.pegz.tutorials.rightcourt.serve.service.PlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/right")
public class ServeController {

    private PlayService playService;

    public ServeController(PlayService playService) {
        this.playService = playService;
    }

    @PostMapping("/serve")
    public ResponseEntity<String> startPlay() throws PointException {
        log.info("Starting Serve");
        playService.serve();
        return ResponseEntity.ok("Started game");
    }
}
