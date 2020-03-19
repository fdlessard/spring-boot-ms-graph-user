package io.fdlessard.codebites.msgraph.user;

import com.microsoft.graph.models.extensions.DirectoryObject;
import com.microsoft.graph.models.extensions.Group;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.requests.extensions.IDirectoryObjectCollectionWithReferencesPage;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GroupController {

  @Autowired
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @GetMapping("/group")
  public Map<String, String> group(Principal principal) {

    log.debug("GroupController.group() - principal: {}", principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

    Group group = graphClient
        .groups("9d3a1922-8e29-448c-90bb-b220b226bb2a")
        .buildRequest()
        .get();

    return MsGraphUtils.msGraphGroupToGroupMap(group);
  }

  @GetMapping("/groups/{id}")
  public Map<String, String> groups(@PathVariable String id, Principal principal) {

    log.debug("GroupController.groups({}) - principal: {}", id, principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

    Group group = graphClient
        .groups(id)
        .buildRequest()
        .get();

    return MsGraphUtils.msGraphGroupToGroupMap(group);
  }

  @GetMapping("/groupMembers")
  public List<Map<String, String>> groupMembers(Principal principal) {
    log.debug("GroupController.groupMembers() - principal: {}", principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

/*    List<Option> requestOptions = new ArrayList<Option>();
    requestOptions.add(new QueryOption("$expand", "eq('@odata.type','microsoft.graph.user')"));*/
    IDirectoryObjectCollectionWithReferencesPage directoryObjectPages = graphClient
        .groups("9d3a1922-8e29-448c-90bb-b220b226bb2a")
        .members()
        .buildRequest()
        .get();

    List<DirectoryObject> userDirectoryObjects = directoryObjectPages.getCurrentPage()
        .stream()
        .filter(MsGraphUtils::isMsGraphUser)
        .collect(Collectors.toList());

    return userDirectoryObjects.stream()
        .map(MsGraphUtils::directoryObjectToUserMap)
        .collect(Collectors.toList());
  }

  @GetMapping("/deleteAGroupMember")
  public List<Map<String, String>> deleteAGroupMember(Principal principal) {
    log.debug("GroupController.deleteAGroupMember() - principal: {}", principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

    graphClient
        .groups("9d3a1922-8e29-448c-90bb-b220b226bb2a")
        .members("81a77119-796e-4425-9671-f6231d8193c6")
        .reference()
        .buildRequest()
        .delete();

    IDirectoryObjectCollectionWithReferencesPage directoryObjectPages = graphClient
        .groups("9d3a1922-8e29-448c-90bb-b220b226bb2a")
        .members()
        .buildRequest()
        .get();

    List<DirectoryObject> userDirectoryObjects = directoryObjectPages.getCurrentPage()
        .stream()
        .filter(MsGraphUtils::isMsGraphUser)
        .collect(Collectors.toList());

    return userDirectoryObjects.stream()
        .map(MsGraphUtils::directoryObjectToUserMap)
        .collect(Collectors.toList());

  }

  @GetMapping("/addAGroupMember")
  public List<Map<String, String>> addAGroupMember(Principal principal) {
    log.debug("GroupController.addAGroupMember() - principal: {}", principal);

    IGraphServiceClient graphClient = MsGraphUtils
        .buildGraphClientService(oAuth2AuthorizedClientService, principal);

    User user = graphClient
        .users("81a77119-796e-4425-9671-f6231d8193c6")
        .buildRequest()
        .get();

    graphClient
        .groups("9d3a1922-8e29-448c-90bb-b220b226bb2a")
        .members()
        .references()
        .buildRequest()
        .post(user);

    IDirectoryObjectCollectionWithReferencesPage directoryObjectPages = graphClient
        .groups("9d3a1922-8e29-448c-90bb-b220b226bb2a")
        .members()
        .buildRequest()
        .get();

    List<DirectoryObject> userDirectoryObjects = directoryObjectPages.getCurrentPage()
        .stream()
        .filter(MsGraphUtils::isMsGraphUser)
        .collect(Collectors.toList());

    return userDirectoryObjects.stream()
        .map(MsGraphUtils::directoryObjectToUserMap)
        .collect(Collectors.toList());
  }

}