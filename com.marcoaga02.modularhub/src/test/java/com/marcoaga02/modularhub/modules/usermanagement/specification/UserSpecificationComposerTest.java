package com.marcoaga02.modularhub.modules.usermanagement.specification;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.specification.filter.UserFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSpecificationComposerTest {

    @Mock
    private UserFilter f1;

    @Mock
    private UserFilter f2;

    @Test
    void shouldReturnSpecificationEvenWhenNoFilterIsActive() {
        when(f1.apply(any())).thenReturn(null);
        when(f2.apply(any())).thenReturn(null);

        UserSpecificationComposer composer = new UserSpecificationComposer(List.of(f1, f2));

        assertThat(composer.compose(new UserCriteriaDTO())).isNotNull();
    }

    @Test
    void shouldReturnSpecificationEvenWhenSomeFiltersAreInactive() {
        when(f1.apply(any())).thenReturn((root, query, cb) -> cb.conjunction());
        when(f2.apply(any())).thenReturn(null);

        UserSpecificationComposer composer = new UserSpecificationComposer(List.of(f1, f2));

        assertThat(composer.compose(new UserCriteriaDTO())).isNotNull();
    }

    @Test
    void shouldReturnSpecificationWhenAllFiltersAreActive() {
        when(f1.apply(any())).thenReturn((root, query, cb) -> cb.conjunction());
        when(f2.apply(any())).thenReturn((root, query, cb) -> cb.conjunction());

        UserSpecificationComposer composer = new UserSpecificationComposer(List.of(f1, f2));

        assertThat(composer.compose(new UserCriteriaDTO())).isNotNull();
    }

    @Test
    void shouldReturnSpecificationWhenNoFiltersAreRegistered() {
        UserSpecificationComposer composer = new UserSpecificationComposer(List.of());

        assertThat(composer.compose(new UserCriteriaDTO())).isNotNull();
    }

    @Test
    void shouldPassCriteriaToEveryFilter() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("mario");

        UserSpecificationComposer composer = new UserSpecificationComposer(List.of(f1, f2));
        composer.compose(criteria);

        verify(f1).apply(criteria);
        verify(f2).apply(criteria);
    }

}