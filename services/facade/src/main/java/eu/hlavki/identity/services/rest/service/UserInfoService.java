package eu.hlavki.identity.services.rest.service;

import eu.hlavki.identity.services.rest.model.UserInfo;
import eu.hlavki.identity.services.rest.security.AuthzRole;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("user")
public class UserInfoService {

    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Context
    private OidcClientTokenContext oidcContext;


    public UserInfoService() {
    }


    @GET
    public UserInfo getUserInfo() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        URI profilePicture = resizeProfilePicture(userInfo.getPicture());
        Set<String> roles = (Set) userInfo.getProperty("securityRoles");
        boolean amAdmin = roles.contains(AuthzRole.ADMIN);
        return new UserInfo(userInfo.getName(), userInfo.getEmail(), amAdmin, profilePicture);
    }


    private URI resizeProfilePicture(String originalUri) {
        String url = StringEscapeUtils.unescapeJava(originalUri);
        log.info("Profile URL: {}", url);
        return UriBuilder.fromUri(url).queryParam("sz", "100").build();
    }
}
