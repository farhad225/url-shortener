package org.dkb.server.model.response;

import javax.validation.constraints.NotNull;


public record CreateUrlResponse(@NotNull String encodedUrl) {
}
