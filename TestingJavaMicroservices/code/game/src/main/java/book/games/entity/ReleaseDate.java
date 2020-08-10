package book.games.entity;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public final class ReleaseDate {

    private String platformName;
    private LocalDate releaseDate;

    public ReleaseDate() {
        super();
    }

    public ReleaseDate(String platformName, LocalDate releaseDate) {
        this.platformName = platformName;
        this.releaseDate = releaseDate;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getPlatformName() {
        return platformName;
    }
}
