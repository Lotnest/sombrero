package dev.lotnest.sombrero.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spotify")
@Slf4j
public class SpotifyController {

    @GetMapping("/codeCallback")
    public void codeCallback(@PathVariable @NotNull String code) {
        log.info("Received code callback: {}", code);
    }
}
