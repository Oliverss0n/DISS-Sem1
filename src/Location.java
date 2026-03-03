public class Location {
    private final LocationType type;
    private boolean visited;

    public Location(LocationType type) {
        this.type = type;
        this.visited = false;
    }

    public void markAsVisited() {
        this.visited = true;
    }

    public void reset() {
        this.visited = false;
    }

    public LocationType getType() { return type; }
    public boolean isVisited() { return visited; }
}