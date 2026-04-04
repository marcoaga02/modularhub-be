package com.marcoaga02.modularhub.modules.usermanagement.specification;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.specification.filter.UserFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class UserSpecificationComposer {

    private final List<UserFilter> filters;

    private static final Specification<User> EMPTY =
            (root, query, cb) -> cb.conjunction();


    public UserSpecificationComposer(List<UserFilter> filters) {
        this.filters = filters;
    }

    public Specification<User> compose(UserCriteriaDTO criteria) {
        return filters.stream()
                .map(f -> f.apply(criteria))
                .filter(Objects::nonNull)
                .reduce(EMPTY, Specification::and);
    }

}
