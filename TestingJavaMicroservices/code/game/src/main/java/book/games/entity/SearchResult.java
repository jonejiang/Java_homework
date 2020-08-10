package book.games.entity;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.Serializable;
import java.util.Objects;

public class SearchResult implements Serializable {

    private long id;

    private String name;

    public SearchResult() {
    }

    public SearchResult(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public JsonObject convertToJson() {
        return Json.createObjectBuilder().add("id", this.id).add
                ("name", this.name).build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SearchResult result = (SearchResult) o;
        return id == result.id && Objects.equals(name, result.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
