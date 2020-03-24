package io.fdlessard.codebites.msgraph.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.graph.auth.confidentialClient.ClientCredentialProvider;
import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.DirectoryObject;
import com.microsoft.graph.models.extensions.Group;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

public class MsGraphUtils {

  private MsGraphUtils() {
  }

  public static Map<String, String> msGraphUserToUserMap(User user) {

    Map<String, String> userMap = new HashMap<>();
    if (user == null) {
      return userMap;
    }

    userMap.put("id", user.id);
    userMap.put("userPrincipalName", user.userPrincipalName);
    userMap.put("displayName", user.displayName);
    userMap.put("givenName", user.givenName);
    userMap.put("surname", user.surname);
    userMap.put("mail", user.mail);

    return userMap;
  }

  public static Map<String, String> directoryObjectToUserMap(DirectoryObject directoryObject) {

    Map<String, String> userMap = new HashMap<>();
    if (directoryObject == null) {
      return userMap;
    }

    JsonObject jsonObject = directoryObject.getRawObject();
    userMap.put("id", getAsString(jsonObject, "id"));
    userMap.put("userPrincipalName", getAsString(jsonObject, "userPrincipalName"));
    userMap.put("displayName", getAsString(jsonObject, "displayName"));
    userMap.put("givenName", getAsString(jsonObject, "givenName"));
    userMap.put("surname", getAsString(jsonObject, "surname"));
    userMap.put("mail", getAsString(jsonObject, "mail"));

    return userMap;
  }

  public static Map<String, String> msGraphGroupToGroupMap(Group group) {

    Map<String, String> userMap = new HashMap<>();
    if (group == null) {
      return userMap;
    }

    userMap.put("id", group.id);
    userMap.put("displayName", group.displayName);
    userMap.put("description", group.description);

    return userMap;
  }

  public static boolean isMsGraphUser(DirectoryObject directoryObject) {

    if (directoryObject == null) {
      return false;
    }

    return StringUtils.equals(directoryObject.oDataType, "#microsoft.graph.user");
  }

  public static String getAsString(JsonObject jsonObject, String key) {

    if (jsonObject == null || StringUtils.isBlank(key)) {
      return null;
    }
    JsonElement jsonElement = jsonObject.get(key);
    if (jsonElement == null || jsonElement.isJsonNull()) {
      return null;
    }

    return jsonElement.getAsString();
  }


  public static IGraphServiceClient buildGraphClientService(
      OAuth2AuthorizedClientService oAuth2AuthorizedClientService, Principal principal) {

    OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService
        .loadAuthorizedClient("ms-graph-api", principal.getName());
    String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
    SimpleAuthProvider authProvider = new SimpleAuthProvider(accessToken);

    // Create default logger to only log errors
    DefaultLogger logger = new DefaultLogger();
    logger.setLoggingLevel(LoggerLevel.DEBUG);

    // Build a Graph client
    return GraphServiceClient.builder()
        .authenticationProvider(authProvider)
        .logger(logger)
        .buildClient();
  }

  public static IGraphServiceClient buildGraphClientService2(
      OAuth2AuthorizedClientService oAuth2AuthorizedClientService, Principal principal) {

    ClientCredentialProvider authProvider = new ClientCredentialProvider(
        "3374cf56-2250-4efd-ad82-97deb40e80c4",
        Arrays.asList("https://graph.microsoft.com/user.read"),
        "/pmlOF=?]6WBK9I2Riez0Y@.U8LL[:B=",
        "a805eb36-3833-4902-88c2-5cb74b2cbcbf",
        NationalCloud.Global);


    // Create default logger to only log errors
    DefaultLogger logger = new DefaultLogger();
    logger.setLoggingLevel(LoggerLevel.DEBUG);

    // Build a Graph client
    return GraphServiceClient.builder()
        .authenticationProvider(authProvider)
        .logger(logger)
        .buildClient();
  }
}
