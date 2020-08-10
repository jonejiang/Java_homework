package book.comments.boundary;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Path("/comments")
@Singleton
@Lock(LockType.READ)
public class CommentsInternalResource {

    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServiceVersion() throws IOException {
        final InputStream manifestStream = CommentsInternalResource
                .class.getResourceAsStream("/META-INF/MANIFEST.MF");
        final Manifest manifest = new Manifest();
        manifest.read(manifestStream);
        final Attributes mainAttribs = manifest.getMainAttributes();
        final String version = mainAttribs.getValue
                ("Implementation-Version");

        if (version != null) {
            return Response.ok(version).build();
        } else {
            return Response.ok().build();
        }
    }

}
