package com.marcoaga02.modularhub.shared.model;

import com.marcoaga02.modularhub.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_preferences")
public class AccountPreferences extends BaseEntity {

    @Column(name = "identity_id")
    private String identityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Override
    protected Class<? extends BaseEntity> getEntityClass() {
        return AccountPreferences.class;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
