package de.cgabrisch.rock_paper_scissors.shared.player;

import javax.validation.constraints.NotEmpty;

public record PlayerRegistration(@NotEmpty String name, @NotEmpty String clientUrl) {

}
