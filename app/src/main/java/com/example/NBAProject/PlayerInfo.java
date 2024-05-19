package com.example.NBAProject;

public class PlayerInfo {
    String Name;
    String Age;
    String Assist;
    Integer Height;
    String POS;
    String Points;
    String Rebound;
    Integer Salary;
    String Steal;
    Float Weight;
    String Block;
    String photo;

     public PlayerInfo(){

     }

    public PlayerInfo(String name, String age, String assist, Integer height, String POS, String points, String rebound, Integer salary, String steal, Float weight, String block, String photo) {
        Name = name;
        Age = age;
        Assist = assist;
        Height = height;
        this.POS = POS;
        Points = points;
        Rebound = rebound;
        Salary = salary;
        Steal = steal;
        Weight = weight;
        Block = block;
        this.photo = photo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getAssist() {
        return Assist;
    }

    public void setAssist(String assist) {
        Assist = assist;
    }

    public Integer getHeight() {
        return Height;
    }

    public void setHeight(Integer height) {
        Height = height;
    }

    public String getPOS() {
        return POS;
    }

    public void setPOS(String POS) {
        this.POS = POS;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        Points = points;
    }

    public String getRebound() {
        return Rebound;
    }

    public void setRebound(String rebound) {
        Rebound = rebound;
    }

    public Integer getSalary() {
        return Salary;
    }

    public void setSalary(Integer salary) {
        Salary = salary;
    }

    public String getSteal() {
        return Steal;
    }

    public void setSteal(String steal) {
        Steal = steal;
    }

    public Float getWeight() {
        return Weight;
    }

    public void setWeight(Float weight) {
        Weight = weight;
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
