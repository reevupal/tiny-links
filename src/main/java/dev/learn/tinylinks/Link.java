package dev.learn.tinylinks;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "links")
public class Link {
    @Id
    private String code;
    private String targetUrl;
    private Instant createdAt;
    private long clicks;

    protected Link() {}

    public Link(String code, String targetUrl) {
        this.code = code;
        this.targetUrl = targetUrl;
        this.createdAt = Instant.now();
        this.clicks = 0;
    }

    public String getCode() { return code; }
    public String getTargetUrl() { return targetUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public long getClicks() { return clicks; }
}
