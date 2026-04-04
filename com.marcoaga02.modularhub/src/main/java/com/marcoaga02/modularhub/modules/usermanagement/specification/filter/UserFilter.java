package com.marcoaga02.modularhub.modules.usermanagement.specification.filter;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import org.springframework.data.jpa.domain.Specification;

public interface UserFilter {
    Specification<User> apply(UserCriteriaDTO criteria);
}
