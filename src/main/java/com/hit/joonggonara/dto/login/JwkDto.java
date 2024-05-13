package com.hit.joonggonara.dto.login;

public record JwkDto(
        String kid,
        String kty,
        String alg,
        String use,
        String n,
        String e
) {
}
