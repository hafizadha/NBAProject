package com.example.NBAProject.TeamRoster;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

public class PlayerInfo implements Comparable<PlayerInfo>, Parcelable, Serializable {
    //Comparable interface: Object can be compared with another same object for ordering and sorting (used for Priority Queue)
    //Parcelable interface: Store data into a Parcel which can be used to pass between Android activities and services
    //Serializable interface: Alternative to parcelable, but not commonly used for Android projects

    //Attributes of a player
    private String Name;
    private String Age;
    private String Assist;
    private Integer Height;
    private String POS;
    private String Points;
    private String Rebound;
    private Integer Salary;
    private String Steal;
    private Integer Weight;
    private String Block;
    private String photo;
    private String injuryDescription; //Information on the type of injury ( for injury stack )
    private long timestamp; //For injuryStack in database, used as reference to sort players chronologically
    private double compositeScore; //Player's score to be ranked in performance ranking

     public PlayerInfo(){

     }

    public PlayerInfo(String name, String age, String assist, Integer height, String POS, String points, String rebound, Integer salary, String steal, Integer weight, String block, String photo) {
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


    //Implementation of Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    protected PlayerInfo(Parcel in) {
        Height = in.readInt();
        Weight = in.readInt();
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


    //Overriding methods from Parcelable interface
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


    //Getter and setter methods for attributes
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

    public Integer getWeight() {
        return Weight;
    }

    public void setWeight(Integer weight) {
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

    //For injury reserve
    public String getInjuryDescription() {
        return injuryDescription;
    }

    public void setInjuryDescription(String injuryDescription) {
        this.injuryDescription = injuryDescription;
    }

    //For player's ranking performance
    public double getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(double compositeScore) {
        this.compositeScore = compositeScore;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    //Overriding method in Comparable interface
    @Override
    public int compareTo(PlayerInfo o) {
        if(Float.parseFloat(this.getPoints()) < Float.parseFloat(o.getPoints())) {
            return 1;
        } else if (Float.parseFloat(this.getPoints()) > Float.parseFloat(o.getPoints())) {
            return -1;
        } else {
            return 0;
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(Name, Points);
    }
}
