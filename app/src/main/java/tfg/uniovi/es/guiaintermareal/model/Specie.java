package tfg.uniovi.es.guiaintermareal.model;

public class Specie {
    private String title,description,ecology,image;

    public Specie(String title, String description, String ecology, String image) {
        this.title = title;
        this.description = description;
        this.ecology = ecology;
        this.image = image;
    }

    public Specie() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEcology() {
        return ecology;
    }

    public void setEcology(String ecology) {
        this.ecology = ecology;
    }
}

