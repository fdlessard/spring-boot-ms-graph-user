package io.fdlessard.codebites.msgraph.user;

import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import java.security.Principal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

  @Autowired
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @GetMapping("/ui")
  public String home(Principal principal) {

    log.debug("UserController.ui() {} - principal: {}", principal);
    String name = principal.getName();
    return "You made it to protected ui! " + name;
  }

  @GetMapping("/me")
  public Map me(Principal principal) {

    log.debug("UserController.user() - principal: {}", principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

    User user = graphClient
        .me()
        .buildRequest()
        .get();

    return MsGraphUtils.msGraphUserToUserMap(user);
  }

  @GetMapping("/users/{id}")
  public Map users(@PathVariable String id, Principal principal) {

    log.debug("UserController.users({id}) - principal: {}", id, principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

    User user = graphClient
        .users(id)
        .buildRequest()
        .get();

    return MsGraphUtils.msGraphUserToUserMap(user);
  }
}