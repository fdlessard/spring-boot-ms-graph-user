package io.fdlessard.codebites.msgraph.user;

import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

  @GetMapping("/home")
  public String home(Principal principal) {
    log.debug("Called UiController.home() endpoint");
    String name = principal.getName();
    return "You made it to protected ui! " + name;
  }
}