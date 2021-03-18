package similarity;

public class SecurityRecord {

    String id;
    String desc;
    String type;
    String typeofsource;
    String weakness;
    String link;
    String severityScore;
    String severity;

    public SecurityRecord(String id, String desc, String type, String typeofsource, String wekaness, String link, String severityScore, String severity) {
        this.id = id;
        this.desc = desc;
        this.type = type;
        this.typeofsource = typeofsource;
        this.weakness = wekaness;
        this.link = link;
        this.severityScore = severityScore;
        this.severity = severity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeofsource() {
        return typeofsource;
    }

    public void setTypeofsource(String typeofsource) {
        this.typeofsource = typeofsource;
    }

    public String getWeakness() {
        return weakness;
    }

    public void setWeakness(String wekaness) {
        this.weakness = wekaness;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSeverityScore() {
        return severityScore;
    }

    public void setSeverityScore(String severityScore) {
        this.severityScore = severityScore;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
