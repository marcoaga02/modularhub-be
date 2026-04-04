package com.marcoaga02.modularhub.modules.usermanagement.specification.filter;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotDeletedFilterTest {

    private final NotDeletedFilter filter = new NotDeletedFilter();

    @Test
    void shouldAlwaysReturnSpec() {
        assertThat(filter.apply(null)).isNotNull();
    }

    @Test
    void shouldAlwaysReturnSpecEvenWithCriteria() {
        assertThat(filter.apply(new UserCriteriaDTO())).isNotNull();
    }

}