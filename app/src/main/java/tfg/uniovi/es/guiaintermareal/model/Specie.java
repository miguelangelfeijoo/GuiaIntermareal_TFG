package tfg.uniovi.es.guiaintermareal.model;

public class Specie {
    private String title,description,image;

    public Specie(String image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
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
}

