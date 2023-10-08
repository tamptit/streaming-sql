package entity;



import java.util.Objects;


public class ResidentCitizen {
    private String id;
    private String personalId;
    private String identity;
    private String name;
    private String gender;
    private String blood;
    private String birthDate;
    private String nationality;

    public ResidentCitizen() {
    }

    public ResidentCitizen(String personalId, String identity, String name, String gender, String blood, String birthDate, String nationality) {
        this.personalId = personalId;
        this.identity = identity;
        this.name = name;
        this.gender = gender;
        this.blood = blood;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public ResidentCitizen(String id, String personalId, String identity, String name, String gender, String blood, String birthDate, String nationality) {
        this.id = id;
        this.personalId = personalId;
        this.identity = identity;
        this.name = name;
        this.gender = gender;
        this.blood = blood;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidentCitizen that = (ResidentCitizen) o;
        return id.equals(that.id) && personalId.equals(that.personalId) && identity.equals(that.identity) && name.equals(that.name) && gender.equals(that.gender) && blood.equals(that.blood) && birthDate.equals(that.birthDate) && nationality.equals(that.nationality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personalId, identity, name, gender, blood, birthDate, nationality);
    }

    @Override
    public String toString() {
        return "NatCitizenDto{" + "id='" + id + '\'' +", personalId='" + personalId + '\'' +
                ", identity='" + identity + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", blood='" + blood + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }


}
