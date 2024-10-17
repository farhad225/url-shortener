package org.dkb.hash.generation.service.model.response;

import javax.validation.constraints.NotNull;


public record CreateHashResponse(@NotNull String hash) {
}
