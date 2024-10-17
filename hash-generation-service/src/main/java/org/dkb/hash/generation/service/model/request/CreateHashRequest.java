package org.dkb.hash.generation.service.model.request;

import javax.validation.constraints.NotNull;


public record CreateHashRequest(@NotNull String originalUrl) {
}
