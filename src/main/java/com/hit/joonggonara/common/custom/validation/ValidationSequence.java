package com.hit.joonggonara.common.custom.validation;

import jakarta.validation.GroupSequence;

@GroupSequence({ValidationGroups.NotBlankGroup.class, ValidationGroups.EmailGroup.class, ValidationGroups.PasswordPatternGroup.class})
public interface ValidationSequence {
}
