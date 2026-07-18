package com.techshop.inventorypos.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;

    @Column(name = "membership_type")
    private String membershipType = "Standard"; // Standard, Silver, Gold

    public Member() {}

    public Member(String name, String phone, String email, String membershipType) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.membershipType = membershipType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    @Override
    public String toString() {
        return name + " (" + membershipType + ")";
    }
}
