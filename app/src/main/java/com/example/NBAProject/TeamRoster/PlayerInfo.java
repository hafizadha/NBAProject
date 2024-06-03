package com.example.NBAProject.TeamRoster;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

public class PlayerInfo implements Parcelable, Serializable {
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
    private String injuryDescription;

    private double compositeScore;

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


    @Override
    public int describeContents() {
        return 0;
    }

    protected PlayerInfo(Parcel in) {
        Height = in.readInt();
        Weight = in.readFloat();
        Age = in.readString();
        Points = in.readString();
        Steal = in.readString();
        Assist = in.readString();
        POS = in.readString();
        Salary = in.readInt();
        Rebound = in.readString();
        Block = in.readString();
        Name = in.readString();
        photo = in.readString();
        injuryDescription = in.readString(); // Read the new field
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Height);
        dest.writeFloat(Weight);
        dest.writeString(Age);
        dest.writeString(Points);
        dest.writeString(Steal);
        dest.writeString(Assist);
        dest.writeString(POS);
        dest.writeLong(Salary);
        dest.writeString(Rebound);
        dest.writeString(Block);
        dest.writeString(Name);
        dest.writeString(photo);
        dest.writeString(injuryDescription); // Write the new field
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PlayerInfo other = (PlayerInfo) obj;
        // Customize this method based on the attributes you want to compare
        return Objects.equals(this.Name, other.Name) &&
                Objects.equals(this.POS, other.POS) &&
                // Compare other attributes as needed
                // For primitive types like int, use == for comparison
                Objects.equals(this.Height, other.Height) &&
                Objects.equals(this.Weight, other.Weight);
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

    public String getInjuryDescription() {
        return injuryDescription;
    }

    public void setInjuryDescription(String injuryDescription) {
        this.injuryDescription = injuryDescription;
    }

    public double getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(double compositeScore) {
        this.compositeScore = compositeScore;
    }
}
