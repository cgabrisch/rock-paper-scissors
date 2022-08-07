package de.cgabrisch.rock_paper_scissors.server;

import javax.validation.constraints.NotEmpty;

public record PlayerRegistration(@NotEmpty String name, @NotEmpty String clientUrl) {

}
