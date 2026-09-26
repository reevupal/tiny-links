package dev.learn.tinylinks;

import java.net.URI;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkService {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 7;
    private final SecureRandom random = new SecureRandom();
    private final LinkRepository repository;

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Link create(String targetUrl) {
        validateUrl(targetUrl);
        for (int attempt = 0; attempt < 5; attempt++) {
            String code = randomCode();
            if (!repository.existsById(code)) {
                return repository.save(new Link(code, targetUrl));
            }
        }
        throw new IllegalStateException("Could not allocate a short code; retry the request");
    }

    @Transactional(readOnly = true)
    public Link find(String code) {
        return repository.findById(code).orElseThrow(LinkNotFoundException::new);
    }

    @Transactional
    public Link recordClickAndFind(String code) {
        if (repository.incrementClicks(code) == 0) throw new LinkNotFoundException();
        return find(code);
    }

    private String randomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        return code.toString();
    }

    private void validateUrl(String value) {
        if (value == null || value.length() > 2048) throw new InvalidUrlException();
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            if (uri.getHost() == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw new InvalidUrlException();
            }
        } catch (IllegalArgumentException exception) {
            throw new InvalidUrlException();
        }
    }
}
