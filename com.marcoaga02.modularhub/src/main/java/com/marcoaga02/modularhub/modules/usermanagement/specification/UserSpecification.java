package com.marcoaga02.modularhub.modules.usermanagement.specification;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {
    private UserSpecification() {}

    public static Specification<User> byCriteria(UserCriteriaDTO criteria) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.isNull(root.get("deletedOn")));

            if (criteria == null) {
                return predicate;
            }

            if (StringUtils.hasText(criteria.getText())) {
                String pattern = "%" + criteria.getText().toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("firstname")), pattern),
                                cb.like(cb.lower(root.get("lastname")), pattern),
                                cb.like(cb.lower(root.get("email")), pattern)
                        ));
            }

            return predicate;
        };
    }
}
