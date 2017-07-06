package tfg.uniovi.es.guiaintermareal.model;

import com.google.firebase.database.Exclude;
import java.util.ArrayList;

public class Specie {
    private String title, description, ecology, image, habitat, taxonomy;
    private long size;
    private ArrayList<String> references;
    private ArrayList<String> carousel;
    @Exclude
    private boolean subcategory;


    public Specie() {
    }

    public Specie(String title, String description, String ecology, String image, String habitat, String taxonomy, long size, ArrayList<String> references, ArrayList<String> carousel, boolean subcategory) {
        this.title = title;
        this.description = description;
        this.ecology = ecology;
        this.image = image;
        this.habitat = habitat;
        this.taxonomy = taxonomy;
        this.size = size;
        this.references = references;
        this.carousel = carousel;
        this.subcategory = subcategory;
    }

    public String getHabitat() {
        return habitat;
    }

    public String getTaxonomy() {
        return taxonomy;
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

    public String getEcology() {
        return ecology;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public ArrayList<String> getCarousel() {
        return carousel;
    }

    public long getSize() {
        return size;
    }
}

