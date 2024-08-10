package com.hit.joonggonara.dto.login;

import java.util.List;

public record OidcKidDto(
        List<JwkDto> keys
) {
}
