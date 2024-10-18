package org.dkb.server.model.request;

import javax.validation.constraints.NotNull;


public record CreateUrlRequest(@NotNull String originalUrl) {
}
