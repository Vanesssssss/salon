package POJO;

import java.io.Serializable;

public class AddMasterRequest implements Serializable {
    private String login;
    private String password;
    private String lastName;
    private String firstName;
    private String experience;
    private String startWork;
    private String endWork;
    private String specialization;

    public AddMasterRequest() {}

    public AddMasterRequest(String login, String password, String lastName, String firstName, String experience, String startWork, String endWork, String specialization) {
        this.login = login;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.experience = experience;
        this.startWork = startWork;
        this.endWork = endWork;
        this.specialization = specialization;
    }

    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public String getLastName() {
        return lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getExperience() {
        return experience;
    }
    public String getStartWork() {
        return startWork;
    }
    public String getEndWork() {
        return endWork;
    }
    public String getSpecialization() {
        return specialization;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setExperience(String experience) {
        this.experience = experience;
    }
    public void setStartWork(String startWork) {
        this.startWork = startWork;
    }
    public void setEndWork(String endWork) {
        this.endWork = endWork;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "AddMasterRequest{" +
                "login='" + login + '\'' +
                ", password='[REDACTED]'" +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", experience='" + experience + '\'' +
                ", startWork='" + startWork + '\'' +
                ", endWork='" + endWork + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
