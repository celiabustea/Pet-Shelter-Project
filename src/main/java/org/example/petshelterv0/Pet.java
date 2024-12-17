package org.example.petshelterv0;

public class Pet {
    private String name;
    private int age;
    private String imagePath;
    private String breed;
    private String description;

    public Pet(String name, int age, String imagePath, String breed, String description) {
        this.name = name;
        this.age = age;
        this.imagePath = imagePath;
        this.breed = breed;
        this.description = description;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getImagePath() { return imagePath; }
    public String getBreed() { return breed; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Pet [name=" + name + ", age=" + age + ", imagePath=" + imagePath +
                ", breed=" + breed + ", description=" + description + "]";
    }
}
