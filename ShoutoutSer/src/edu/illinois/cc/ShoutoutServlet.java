package edu.illinois.cc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

@WebServlet("/ShoutoutServlet")
public class ShoutoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String AWS_ACCESS_KEY = "AKIAJ5TMYC3O6NA4YRZA";
	private static final String AWS_SECRET_KEY = "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";
	private static final String DELIMITER = "\t";
	private static S3Service s3Service = null;
	private final String myBucketName = "Team9Bucket";

	public ShoutoutServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (!isS3ServiceExists())
			setS3Service();

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		out.println("<HTML>");
		out.println("<HEAD><TITLE>SHOUT OUT TEAM9</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<font color=\"orange\"><H1> UIUC SHOUT OUTS </H1></font> <hr size=3>");

		S3Object[] objects;
		try {
			objects = s3Service.listObjects(myBucketName);
		} catch (S3ServiceException e1) {
			out.println(e1.getMessage());
			out.close();
			return;
		}
		
		for (int i = 0; i < objects.length; i++) {

			
			BufferedReader br;
			try {

				S3Object object = s3Service.getObject(myBucketName, objects[i].getKey());
				
				br = new BufferedReader(new InputStreamReader(
						object.getDataInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					String[] separated = line.split(DELIMITER);

					// separated[0] = user
					// separated[1] = msg

					out.print("<h1>" + separated[1] + "</h1>");
					out.println("<font color=\"green\"><h4> by " + separated[0]
							+ "</h4></font>");
				}

				br.close();
			} catch (Exception e) {
				out.println(e.getMessage());
			}

		}

		out.println("</body></html>");

		// close
		out.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String shoutOut = request.getParameter("shout");
		String name = request.getParameter("name");
		String bucketName = "Team9Bucket";
		
		PrintWriter out = response.getWriter();
		
		try {
			storeInS3Bucket(shoutOut, name, bucketName);
		} catch (NoSuchAlgorithmException e) {
			
			out.print(e.getMessage());
		} catch (S3ServiceException e) {
			out.print(e.getMessage());
		}

		out.print(shoutOut + " by " + name + " was posted.");
		out.close();
	}

	private S3Object[] getObjects() {

		try {
			return s3Service.listObjects(myBucketName);
		} catch (S3ServiceException e) {
			e.printStackTrace();
		}
		return new S3Object[0];
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

	private void storeInS3Bucket(String shoutOut, String name, String bucketName)
			throws NoSuchAlgorithmException, IOException, S3ServiceException {

		if (!isS3ServiceExists())
			setS3Service();
		S3Bucket bucket = null;
		if (!isBucketExists(bucketName))
			bucket = createNewBucket(bucketName);
		else
			bucket = getBucket(bucketName);

		String key = "" + System.currentTimeMillis();
		String value = name + DELIMITER + shoutOut;
		S3Object shoutOutObject = null;
		shoutOutObject = new S3Object(key, value);
		s3Service.putObject(bucket, shoutOutObject);
	}

	private List<String[]> getShoutData(String bucketName) {
		List<String[]> resultList = new ArrayList<String[]>();
		try {
			S3Object[] objects = s3Service.listObjects(bucketName);
			for (int o = 0; o < objects.length; o++) {
				//resultList.add(getDataFromObject(objects[o]));  // This function getDataFromObject doesn't exist

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}
}