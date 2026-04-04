package com.marcoaga02.modularhub.modules.usermanagement.specification.filter;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextSearchFilterTest {

    private final TextSearchFilter filter = new TextSearchFilter();

    @Test
    void shouldReturnNullWhenCriteriaIsNull() {
        assertThat(filter.apply(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenTextIsBlank() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("   ");
        assertThat(filter.apply(criteria)).isNull();
    }

    @Test
    void shouldReturnSpecWhenTextIsPresent() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("mario");
        assertThat(filter.apply(criteria)).isNotNull();
    }

}