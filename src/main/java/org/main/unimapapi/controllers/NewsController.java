package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.News_dto;
import org.main.unimapapi.services.NewsNotificationService;
import org.main.unimapapi.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing news articles.
 *
 * <p><strong>Base URL:</strong> <code>/api/news</code></p>
 *
 * <p>Supports operations for creating, reading, updating, deleting (CRUD) news, and sending push notifications.</p>
 */
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;
    private final NewsNotificationService newsNotificationService;

    /**
     * Retrieves all news articles.
     *
     * @return a list of all {@link News_dto}
     */
    @Operation(summary = "Get all news", description = "Returns a list of all available news entries.")
    @ApiResponse(responseCode = "200", description = "News list retrieved successfully")
    @GetMapping("/all")
    public ResponseEntity<List<News_dto>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    /**
     * Retrieves the latest 10 news articles.
     *
     * @return a list of the latest {@link News_dto}
     */
    @Operation(summary = "Get latest news", description = "Returns the latest 10 news entries.")
    @ApiResponse(responseCode = "200", description = "Latest news list retrieved successfully")
    @GetMapping("/latest")
    public ResponseEntity<List<News_dto>> getLatestNews() {
        return ResponseEntity.ok(newsService.getLatestNews(10));
    }

    /**
     * Retrieves a specific news article by its ID.
     *
     * @param id the ID of the news article
     * @return the found {@link News_dto} or 404 if not found
     */
    @Operation(summary = "Get news by ID", description = "Returns a news article by its ID.")
    @ApiResponse(responseCode = "200", description = "News found")
    @ApiResponse(responseCode = "404", description = "News not found")
    @GetMapping("/{id}")
    public ResponseEntity<News_dto> getNewsById(@PathVariable Long id) {
        News_dto news = newsService.getNewsById(id);
        if (news != null) {
            return ResponseEntity.ok(news);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new news article and sends push notifications to all devices.
     *
     * @param news the news entry to create
     * @return the created {@link News_dto}
     */
    @Operation(summary = "Create news", description = "Creates a new news article and sends a notification to all devices.")
    @ApiResponse(responseCode = "200", description = "News created successfully")
    @PostMapping
    public ResponseEntity<News_dto> createNews(@RequestBody News_dto news) {
        News_dto savedNews = newsService.saveNews(news);

        // Fire-and-forget notification
        CompletableFuture.runAsync(() -> {
            newsNotificationService.sendNewsNotificationToAllDevices(savedNews);
        });

        return ResponseEntity.ok(savedNews);
    }

    /**
     * Updates an existing news article.
     *
     * @param id   the ID of the news to update
     * @param news the new news content
     * @return the updated {@link News_dto}, or 404 if not found
     */
    @Operation(summary = "Update news", description = "Updates a news article by ID.")
    @ApiResponse(responseCode = "200", description = "News updated successfully")
    @ApiResponse(responseCode = "404", description = "News not found")
    @PutMapping("/{id}")
    public ResponseEntity<News_dto> updateNews(@PathVariable Long id, @RequestBody News_dto news) {
        if (!newsService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        news.setId(id.intValue());
        return ResponseEntity.ok(newsService.saveNews(news));
    }


    /**
     * Deletes a news article by ID.
     *
     * @param id the ID of the news article to delete
     * @return 204 No Content if deleted, or 404 if not found
     */
    @Operation(summary = "Delete news", description = "Deletes a news article by its ID.")
    @ApiResponse(responseCode = "204", description = "News deleted successfully")
    @ApiResponse(responseCode = "404", description = "News not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (!newsService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        newsService.deleteNewsById(id);
        return ResponseEntity.noContent().build();
    }
}