package dev.learn.tinylinks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, String> {
    @Modifying
    @Query("update Link l set l.clicks = l.clicks + 1 where l.code = :code")
    int incrementClicks(@Param("code") String code);
}
