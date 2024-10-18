package org.dkb.server.model.response;

import javax.validation.constraints.NotNull;


public record CreateHashResponse(@NotNull String hash) {
}
