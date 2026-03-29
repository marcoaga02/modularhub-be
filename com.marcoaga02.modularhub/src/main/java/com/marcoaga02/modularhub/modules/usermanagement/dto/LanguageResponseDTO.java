package com.marcoaga02.modularhub.modules.usermanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageResponseDTO {

    private String id;

    private String code;

    private String label;

    private Boolean isDefault;

}
