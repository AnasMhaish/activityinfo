package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.Image;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.ResourceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.images.ImagesServiceFactory.makeImage;
import static javax.ws.rs.core.Response.Status.*;

@Path("/submission")
public class FormSubmissionResource {
    private static final Logger LOGGER = Logger.getLogger(FormSubmissionResource.class.getName());

    final private OdkFieldValueParserFactory factory;
    final private ResourceStore locator;
    final private AuthenticationTokenService authenticationTokenService;
    final private BlobFieldStorageService blobFieldStorageService;
    final private OdkFormSubmissionBackupService odkFormSubmissionBackupService;

    @Inject
    public FormSubmissionResource(OdkFieldValueParserFactory factory, ResourceStore locator,
                                  AuthenticationTokenService authenticationTokenService,
                                  BlobFieldStorageService blobFieldStorageService,
                                  OdkFormSubmissionBackupService odkFormSubmissionBackupService) {
        this.factory = factory;
        this.locator = locator;
        this.authenticationTokenService = authenticationTokenService;
        this.blobFieldStorageService = blobFieldStorageService;
        this.odkFormSubmissionBackupService = odkFormSubmissionBackupService;
    }

    @POST @Consumes(MediaType.MULTIPART_FORM_DATA) @Produces(MediaType.TEXT_XML)
    public Response submit(byte bytes[]) {
        ResourceId resourceId = Resources.generateId();
        try {
            odkFormSubmissionBackupService.backup(resourceId, ByteSource.wrap(bytes));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Form submission could not be backed up to GCS", e);
        }

        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(bytes, MediaType.MULTIPART_FORM_DATA);
        MimeMultipart mimeMultipart;
        InputStream inputStream;
        DocumentBuilder documentBuilder;
        Document document;

        try {
            mimeMultipart = new MimeMultipart(byteArrayDataSource);
            inputStream = mimeMultipart.getBodyPart(0).getInputStream();
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(inputStream);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to parse input", e);
            return Response.status(BAD_REQUEST).build();
        }

        document.normalizeDocument();

        NodeList nodeList = document.getElementsByTagName("instanceID");
        Node node = nodeList.getLength() == 1 ? nodeList.item(0) : null;

        // A cascade of various validations of the structure of the submitted form, where node may have been set to null
        if (node != null &&
                "instanceID".equals(node.getNodeName()) &&
                "meta".equals(node.getParentNode().getNodeName()) &&
                "data".equals(node.getParentNode().getParentNode().getNodeName()) &&
                "#document".equals(node.getParentNode().getParentNode().getParentNode().getNodeName()) &&
                node.getParentNode().getParentNode().getParentNode().getParentNode() == null) {
            String instanceId = OdkHelper.extractText(node).replace("-", "");
            Node dataNode = node.getParentNode().getParentNode();

            if (dataNode.hasAttributes() && dataNode.getAttributes().getLength() == 1) {
                String tokenString = OdkHelper.extractText(dataNode.getAttributes().item(0));

                if (tokenString != null && tokenString.length() > 0) {
                    int userId;
                    int formClassId;
                    AuthenticationToken authenticationToken = new AuthenticationToken(tokenString);
                    AuthenticatedUser user;
                    try {
                        userId = authenticationTokenService.getUserId(authenticationToken);
                        formClassId = authenticationTokenService.getFormClassId(authenticationToken);
                        user = new AuthenticatedUser("XYZ", userId, "@");
                    } catch (EntityNotFoundException entityNotFoundException) {
                        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                    } catch (RuntimeException runtimeException) {
                        throw runtimeException;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Resource resource = locator.get(user, CuidAdapter.activityFormClass(formClassId));
                    FormClass formClass = FormClass.fromResource(resource);
                    FormInstance formInstance = new FormInstance(ResourceId.valueOf(instanceId), formClass.getId());

                    for (FormField formField : formClass.getFields()) {
                        OdkFieldValueParser odkFieldValueParser = factory.fromFieldType(formField.getType());
                        Node element = document.getElementsByTagName(
                                OdkHelper.toRelativeFieldName(formField.getId().asString())).item(0);

                        if (element instanceof Element) {
                            try {
                                formInstance.set(formField.getId(), odkFieldValueParser.parse((Element) element));
                            } catch (Exception e) {
                                String text = OdkHelper.extractText(element);

                                if (text == null) {
                                    LOGGER.log(Level.SEVERE, "Malformed Element in form instance prevents parsing", e);
                                } else if (!text.equals("")) {
                                    LOGGER.log(Level.WARNING, "Can't parse form instance contents, storing as text", e);
                                    formInstance.set(formField.getId(), TextValue.valueOf(text));
                                }
                            }
                        }
                    }

                    Map<ResourceId, FieldValue> fieldValueMap = formInstance.getFieldValueMap();
                    for (ResourceId fieldValueId : fieldValueMap.keySet()) {
                        FieldValue fieldValue = fieldValueMap.get(fieldValueId);
                        if (fieldValue instanceof ImageValue) {
                            ImageRowValue imageRowValue = ((ImageValue) fieldValue).getValues().get(0);
                            if (imageRowValue.getFilename() == null) continue;
                            try {
                                ByteSource byteSource = null;
                                String mimeType = null, contentDisposition = null;  // Initialized just for the compiler

                                for (int i = 0; i < mimeMultipart.getCount(); i++) {
                                    BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                                    if (imageRowValue.getFilename().equals(bodyPart.getFileName())) {
                                        Image image = makeImage(ByteStreams.toByteArray(bodyPart.getInputStream()));

                                        contentDisposition = bodyPart.getDisposition();
                                        mimeType = bodyPart.getContentType();
                                        byteSource = ByteSource.wrap(image.getImageData());
                                        imageRowValue.setMimeType(mimeType);
                                        imageRowValue.setHeight(image.getHeight());
                                        imageRowValue.setWidth(image.getWidth());

                                        break;
                                    }
                                }

                                if (byteSource == null) {
                                    LOGGER.log(Level.SEVERE, "Could not find the specified filename");
                                    return Response.status(BAD_REQUEST).build();
                                }

                                blobFieldStorageService.put(user, contentDisposition, mimeType,
                                        new BlobId(imageRowValue.getBlobId()), byteSource);
                            } catch (MessagingException messagingException) {
                                LOGGER.log(Level.SEVERE, "Unable to parse input", messagingException);
                                return Response.status(BAD_REQUEST).build();
                            } catch (IOException ioException) {
                                LOGGER.log(Level.SEVERE, "Could not write image to GCS", ioException);
                                return Response.status(SERVICE_UNAVAILABLE).build();
                            }

                            formInstance.set(fieldValueId, fieldValue);
                        }
                    }

                    locator.create(user, formInstance.asResource().set("backupBlobId", resourceId.asString()));
                    return Response.status(CREATED).build();
                }
            }
        }

        return Response.status(BAD_REQUEST).build();
    }
}
