package it.smartcommunitylab.climb.domain.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import it.smartcommunitylab.climb.domain.common.Utils;

@Component
public class DocumentManager {
	private AmazonS3 s3;

	@Autowired
	@Value("${storage.s3.bucketName}")
	private String bucketName;
	
	@Value("${s3.region}")
	private String s3Region;
	
	@Value("${s3.endpoint}")
	private String s3Endpoint;
	
	@SuppressWarnings("deprecation")
	@PostConstruct
	public void init() {
		//this.s3 = new AmazonS3Client(new ProfileCredentialsProvider());
		this.s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider())
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Endpoint, s3Region))
				.withPathStyleAccessEnabled(true)
				.build();
	}
	
	public String uploadFile(MultipartFile file) throws IOException {
		String fileKey = Utils.getUUID();
		File tmpFile = createTmpFile(file);
		PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, tmpFile);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.addUserMetadata("x-amz-meta-title", StringEscapeUtils.escapeJson(file.getOriginalFilename()));
    request.setMetadata(metadata);
    request.setCannedAcl(CannedAccessControlList.PublicRead);
		s3.putObject(request);
		String url = s3.getUrl(bucketName, fileKey).toExternalForm();
		tmpFile.delete();
		return url;
	}
	
	private File createTmpFile(MultipartFile file) throws IOException {
		Path tempFile = Files.createTempFile("climb-kgg", ".tmp");
		tempFile.toFile().deleteOnExit();
		Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
		return tempFile.toFile();
	}

}
