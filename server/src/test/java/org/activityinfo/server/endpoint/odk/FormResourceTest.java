package org.activityinfo.server.endpoint.odk;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.endpoint.odk.xform.Html;
import org.activityinfo.service.lookup.ReferenceProvider;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.ResourceStore;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class FormResourceTest {

    private FormResource resource;

    @Before
    public void setUp() throws IOException {
        ResourceStore store = new ResourceStore() {
            @Override
            public ResourceCursor openCursor(ResourceId formClassId) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Resource get(ResourceId resourceId) {
                throw new UnsupportedOperationException();
            }
        };
        Provider<AuthenticatedUser> authProvider = Providers.of(new AuthenticatedUser("", 123, "jorden@bdd.com"));
        OdkTypeAdapterFactory factory = new OdkTypeAdapterFactory(new ReferenceProvider());
        resource = new FormResource(store, authProvider, factory);
    }

    @Test
    public void getBlankForm() throws Exception {
        Response form = this.resource.form(1);
        File file = new File(targetDir(), "form.xml");
        JAXBContext context = JAXBContext.newInstance(Html.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(form.getEntity(), file);
        validate(file);
    }

    private File targetDir() {
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath + "../../target");
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

    public void validate(File file) throws Exception {


        URL validatorJar = Resources.getResource(FormResourceTest.class, "odk-validate-1.4.3.jar");
        String[] command = {"java", "-jar", Paths.get(validatorJar.toURI()).toString(), file.getAbsolutePath()};

        System.out.println(Joiner.on(' ').join(command));

        ProcessBuilder validator = new ProcessBuilder(command);
        validator.inheritIO();
        int exitCode = validator.start().waitFor();

        if(exitCode != 0) {
            System.out.println("Offending XML: " + Files.toString(file, Charsets.UTF_8));
        }

        assertThat(exitCode, equalTo(0));
    }
}
