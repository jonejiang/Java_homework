package book.games.entity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// tag::orm[]
@Entity
public class Game implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Version
    @Column(name = "version")
    private int version;

    @Column // <1>
    private String title;

    @Column
    private String cover;

    @ElementCollection
    @CollectionTable(name = "ReleaseDate", joinColumns =
    @JoinColumn(name = "OwnerId"))
    private List<ReleaseDate> releaseDates = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "Publisher", joinColumns = @JoinColumn
            (name = "OwnerId"))
    private List<String> publishers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "Developer", joinColumns = @JoinColumn
            (name = "OwnerId"))
    private List<String> developers = new ArrayList<>();
    // end::orm[]

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Game)) {
            return false;
        }
        final Game other = (Game) obj;
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getCover() {
        return null != cover ? cover : "upload.wikimedia" + "" +
                ".org/wikipedia/commons/b/b7" +
                "/No_free_shield_available.png";
    }

    public void setCover(final String cover) {
        this.cover = cover;
    }

    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(final List<ReleaseDate>
                                        releaseDates) {
        this.releaseDates = releaseDates;
    }

    public void addReleaseDate(final ReleaseDate releaseDate) {
        this.releaseDates.add(releaseDate);
    }

    public void setDevelopers(final List<String> developers) {
        this.developers = developers;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void addDeveloper(final String developer) {
        this.developers.add(developer);
    }

    public void setPublishers(final List<String> publishers) {
        this.publishers = publishers;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void addPublisher(final String publisher) {
        this.publishers.add(publisher);
    }

    // tag::domain[]

    public JsonObject convertToJson() { // <2>

        final JsonArrayBuilder developers = Json.createArrayBuilder();
        this.getDevelopers().forEach(developers::add);

        final JsonArrayBuilder publishers = Json.createArrayBuilder();
        this.getPublishers().forEach(publishers::add);

        final JsonArrayBuilder releaseDates = Json
                .createArrayBuilder();
        this.getReleaseDates().forEach(releaseDate -> {
            final String platform = releaseDate.getPlatformName();
            final String date = releaseDate.getReleaseDate().format
                    (DateTimeFormatter.ISO_DATE);
            releaseDates.add(Json.createObjectBuilder().add
                    ("platform", platform).add("release_date", date));
        });

        return Json.createObjectBuilder().add("id", this.getId())
                .add("title", this.getTitle()).add("cover", this
                        .getCover()).add("developers", developers)
                .add("publishers", publishers).add("release_dates",
                        releaseDates).build();
    }
    // end::domain[]

    public static Game fromJson(final JsonArray jsonObject) {
        final Game game = new Game();

        final JsonObject gameJsonObject = jsonObject.getJsonObject(0);

        game.setId((long) gameJsonObject.getInt("id"));
        game.setTitle(gameJsonObject.getString("name"));

        if (gameJsonObject.containsKey("cover")) {
            final JsonObject cover = gameJsonObject.getJsonObject
                    ("cover");
            game.setCover(cover.getString("url", ""));
        }

        if (gameJsonObject.containsKey("release_dates")) {
            final JsonArray releaseDates = gameJsonObject
                    .getJsonArray("release_dates");
            releaseDates.stream().map(jsonValue -> {
                JsonObject releaseDateJson = (JsonObject) jsonValue;
                return new ReleaseDate(releaseDateJson.getString
                        ("platform_name", ""), LocalDate.parse
                        (releaseDateJson.getString("release_date",
                                "1950-08-25"), DateTimeFormatter
                                .ISO_DATE));
            }).forEach(game::addReleaseDate);
        }

        if (gameJsonObject.containsKey("companies")) {
            final JsonArray companiesJson = gameJsonObject
                    .getJsonArray("companies");

            //Add developers
            companiesJson.stream().filter(jsonValue -> {
                JsonObject companyJson = (JsonObject) jsonValue;
                return companyJson.getBoolean("developer");
            }).map(jsonValue -> {
                JsonObject companyJson = (JsonObject) jsonValue;
                return companyJson.getString("name", "");
            }).forEach(game::addDeveloper);

            //Add publishers
            companiesJson.stream().filter(jsonValue -> {
                JsonObject companyJson = (JsonObject) jsonValue;
                return companyJson.getBoolean("publisher");
            }).map(jsonValue -> {
                JsonObject companyJson = (JsonObject) jsonValue;
                return companyJson.getString("name", "");
            }).forEach(game::addPublisher);
        }

        return game;
    }

    // tag::orm[]
}
// end::orm[]