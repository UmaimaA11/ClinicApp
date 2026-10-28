package com.clinic.app.malak.models;

/**
 * Modèle représentant un médecin.
 * Contient le nom, la spécialité, l'image et l'état de sélection.
 */
public class Doctor {

    private int id; // ID du médecin (userId)
    private String name; // Nom complet du médecin
    private String speciality; // Spécialité du médecin
    private int image; // Ressource drawable pour la photo
    private boolean isSelected; // Indique si le médecin est sélectionné

    // Constructeur
    public Doctor(int id, String name, String speciality, int image) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.image = image;
        this.isSelected = false; // Par défaut non sélectionné
    }

    // ----- Getters -----
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public int getImage() {
        return image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    // ----- Setters -----
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
