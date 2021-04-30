package similarity;

public class Bug {

    int security;
    String id;
    String description;
    String title;
    String date;
    String bug;

    public Bug(int security, String id, String description, String title, String date) {
        this.security = security;
        this.id = id;
        this.description = description;
        this.title = title;
        this.date = date;
    }

    public Bug(String bug, String description){
        this.bug = bug;
        this.description = description;
    }

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBug() {
        return bug;
    }

    public void setBug(String bug) {
        this.bug = bug;
    }
}
