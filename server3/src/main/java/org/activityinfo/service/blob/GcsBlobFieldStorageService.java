package org.activityinfo.service.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.DeploymentEnvironment;
import org.activityinfo.server.util.blob.DevAppIdentityService;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.gcs.GcsAppIdentityServiceUrlSigner;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.Channels;

public class GcsBlobFieldStorageService implements BlobFieldStorageService {

    private final String bucketName;
    private AppIdentityService appIdentityService;

    @Inject
    public GcsBlobFieldStorageService(DeploymentConfiguration config) {
        this.bucketName = config.getBlobServiceBucketName();
        appIdentityService = DeploymentEnvironment.isAppEngineDevelopment() ?
                new DevAppIdentityService(config) : AppIdentityServiceFactory.getAppIdentityService();
    }

    @Override
    public UploadCredentials getUploadCredentials(ResourceId userId, BlobId blobId) {
        GcsUploadCredentialBuilder builder = new GcsUploadCredentialBuilder(appIdentityService);
        builder.setBucket(bucketName);
        builder.setKey(blobId.asString());
        builder.setMaxContentLengthInMegabytes(5);
        return builder.build();
    }

    @Override
    public URI getBlobUrl(BlobId blobId) {
        GcsAppIdentityServiceUrlSigner signer = new GcsAppIdentityServiceUrlSigner();
        try {
            return new URI(signer.getSignedUrl("GET", bucketName + "/" + blobId.asString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(AuthenticatedUser authenticatedUser, BlobId blobId, ByteSource byteSource) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(bucketName, blobId.asString());
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel channel = gcsService.createOrReplace(gcsFilename, GcsFileOptions.getDefaultInstance());
        try (OutputStream outputStream = Channels.newOutputStream(channel)) {
            byteSource.copyTo(outputStream);
        }
    }
}
