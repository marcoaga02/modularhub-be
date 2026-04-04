package com.marcoaga02.modularhub.modules.usermanagement.specification.filter;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TextSearchFilter implements UserFilter {

    @Override
    public Specification<User> apply(UserCriteriaDTO criteria) {
        if (criteria == null || !StringUtils.hasText(criteria.getText())) {
            return null;
        }

        String pattern = "%" + criteria.getText().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("firstname")), pattern),
                cb.like(cb.lower(root.get("lastname")), pattern),
                cb.like(cb.lower(root.get("email")), pattern)
        );
    }

}
