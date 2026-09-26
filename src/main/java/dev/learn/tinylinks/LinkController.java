package dev.learn.tinylinks;

import java.net.URI;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {
    private final LinkService service;
    private final String baseUrl;

    public LinkController(LinkService service, @Value("${app.base-url}") String baseUrl) {
        this.service = service;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    @PostMapping("/api/links")
    public ResponseEntity<LinkResponse> create(@RequestBody CreateLinkRequest request) {
        Link link = service.create(request.url());
        return ResponseEntity.status(HttpStatus.CREATED).body(response(link));
    }

    @GetMapping("/api/links/{code}")
    public LinkResponse details(@PathVariable String code) {
        return response(service.find(code));
    }

    @GetMapping("/{code:[A-Za-z0-9]{7}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Link link = service.recordClickAndFind(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, link.getTargetUrl())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .build();
    }

    private LinkResponse response(Link link) {
        return new LinkResponse(link.getCode(), baseUrl + "/" + link.getCode(),
                link.getTargetUrl(), link.getCreatedAt(), link.getClicks());
    }

    public record CreateLinkRequest(String url) {}
    public record LinkResponse(String code, String shortUrl, String url, Instant createdAt, long clicks) {}
}
