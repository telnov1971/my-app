package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Table(name = "usr")
@Entity
public class User extends AbstractEntity implements UserDetails {

    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

//    @NotBlank(message = "Пароль не может быть пустым")
//    @EqualsAndHashCode.Exclude
    private String password;
//    @EqualsAndHashCode.Exclude
    private boolean active;

    @Email(message = "Email не корректный")
//    @NotBlank(message = "Email не может быть пустым")
    private String email;

//    @NotBlank(message = "Поле ФИО не может быть пустым")
    private String fio;

//    @NotBlank(message = "Поле Контатный телефон не может быть пустым")
    private String contact;

    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Demand> demands;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garant_id")
    private Garant garant;

    public User() {
    }
    public User(String username, String password, boolean active, String fio, String contact, Set<Role> roles) {
        this.createDate = LocalDateTime.now();
        this.username = username;
        this.password = password;
        this.active = active;
        this.fio = fio;
        this.contact = contact;
        this.roles = roles;
        this.email = "";
        this.demands = null;
        this.activationCode = "";
        this.garant = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.getId(), user.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @Override
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }
    @Override
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }
    public boolean getActive() {return active;}
    public void setActive(boolean active) {
        this.active = active;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getActivationCode() {
        return activationCode;
    }
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public Set<Demand> getDemands() {
        return demands;
    }
    public void setDemands(Set<Demand> demands) {
        this.demands = demands;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }
    public boolean isGarant() {
        return roles.contains(Role.GARANT);
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return isActive();
    }
    public Garant getGarant() {
        return garant;
    }
    public void setGarant(Garant garant) {
        this.garant = garant;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }
}