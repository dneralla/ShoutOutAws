package edu.illinois.cc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

@WebServlet("/ShoutoutServlet")
public class ShoutoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String AWS_ACCESS_KEY = "";
	private static final String AWS_SECRET_KEY = "";
	private static final String DELEMITER = "";
	private static String fileObject = "";
	private static S3Service s3Service = null;

	public ShoutoutServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String shoutOut = request.getParameter("shout");
		String name = request.getParameter("name");
		String bucketName = "Team9Bucket";
		storeInS3Bucket(shoutOut, name, bucketName);

	}

	private void setS3Service() {
		try {
			AWSCredentials awsCredentials = new AWSCredentials(AWS_ACCESS_KEY,
					AWS_SECRET_KEY);
			this.s3Service = new RestS3Service(awsCredentials);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isS3ServiceExists() {
		return this.s3Service != null;
	}

	private boolean isBucketExists(String bucketName) {
		try {
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
			for (int i = 0; i < myBuckets.length; i++)
				if ((myBuckets[i].getName()).equals(bucketName))
					return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	private S3Bucket createNewBucket(String bucketName) {
		try {
			if (s3Service != null)
				return s3Service.createBucket(bucketName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private S3Bucket getBucket(String bucketName) {
		try {
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
			for (int i = 0; i < myBuckets.length; i++)
				if ((myBuckets[i].getName()).equals(bucketName))
					return myBuckets[i];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void storeInS3Bucket(String shoutOut, String name, String bucketName) {

		if (!isS3ServiceExists())
			setS3Service();
		S3Bucket bucket = null;
		if (!isBucketExists(bucketName))
			bucket = createNewBucket(bucketName);
		else
			bucket = getBucket(bucketName);

		String key = "" + System.currentTimeMillis();
		String value = name + "" + DELEMITER + "" + shoutOut;
		S3Object shoutOutObject = null;
		try {
			shoutOutObject = new S3Object(key, value);
			s3Service.putObject(bucket, shoutOutObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String[]> getShoutData(String bucketName) {
		List<String[]> resultList = new ArrayList<String[]>();
		try {
			S3Object[] objects = s3Service.listObjects(getBucket(bucketName));
			for (int o = 0; o < objects.length; o++) {
				resultList.add(getDataFromObject(objects[o]));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}

	private String[] getDataFromObject(S3Object object) {
		// TODO
		return null;
	}

}
