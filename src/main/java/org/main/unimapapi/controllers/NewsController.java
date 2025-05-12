package org.main.unimapapi.controllers;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.News_dto;
import org.main.unimapapi.services.NewsNotificationService;
import org.main.unimapapi.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;
    private final NewsNotificationService newsNotificationService;

    @GetMapping("/all")
    public ResponseEntity<List<News_dto>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @GetMapping("/latest")
    public ResponseEntity<List<News_dto>> getLatestNews() {
        return ResponseEntity.ok(newsService.getLatestNews(10));
    }

    @GetMapping("/{id}")
    public ResponseEntity<News_dto> getNewsById(@PathVariable Long id) {
        News_dto news = newsService.getNewsById(id);
        if (news != null) {
            return ResponseEntity.ok(news);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<News_dto> createNews(@RequestBody News_dto news) {
        News_dto savedNews = newsService.saveNews(news);

        CompletableFuture.runAsync(() -> {
            newsNotificationService.sendNewsNotificationToAllDevices(savedNews);
        });

        return ResponseEntity.ok(savedNews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<News_dto> updateNews(@PathVariable Long id, @RequestBody News_dto news) {
        if (!newsService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        news.setId(id.intValue());
        return ResponseEntity.ok(newsService.saveNews(news));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (!newsService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        newsService.deleteNewsById(id);
        return ResponseEntity.noContent().build();
    }
}