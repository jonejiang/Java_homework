package book.comments.boundary;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

// tag::test[]
@RunWith(Arquillian.class) // <1>
public class CommentsTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, CommentsTest
                .class.getSimpleName() + ".war").addClasses
                (Comments.class);
    }

    @Test
    public void doNothing() {

    }

}
// end::test[]