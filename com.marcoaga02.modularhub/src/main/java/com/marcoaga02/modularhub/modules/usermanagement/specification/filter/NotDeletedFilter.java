package com.marcoaga02.modularhub.modules.usermanagement.specification.filter;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NotDeletedFilter implements UserFilter {

    @Override
    public Specification<User> apply(UserCriteriaDTO criteria) {
        return (root, query, cb) -> cb.isNull(root.get("deletedOn"));
    }

}
