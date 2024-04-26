package com.hit.joonggonara.custom.validation;

import jakarta.validation.GroupSequence;

@GroupSequence({ValidationGroups.NotBlankGroup.class, ValidationGroups.EmailGroup.class})
public interface ValidationSequence {
}
