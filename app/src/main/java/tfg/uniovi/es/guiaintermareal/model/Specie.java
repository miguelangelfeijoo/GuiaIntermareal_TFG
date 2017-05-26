package tfg.uniovi.es.guiaintermareal.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Specie {
    private String title, description, ecology, image, habitat, taxonomy;
    private ArrayList<String> references;

    public Specie(String title, String description, String ecology, String image, String habitat, String taxonomy, ArrayList<String> references) {
        this.title = title;
        this.description = description;
        this.ecology = ecology;
        this.image = image;
        this.habitat = habitat;
        this.taxonomy = taxonomy;
        this.references = references;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
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

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }
}

