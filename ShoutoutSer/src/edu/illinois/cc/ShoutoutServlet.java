package edu.illinois.cc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ShoutoutServlet")
public class ShoutoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   public ShoutoutServlet() {
        super();
   }
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
	   
	   
	   
   
   }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String shoutOut = request.getParameter("shout");
	  String  name    =  request.getParameter("name");
	   
	   storeInS3Bucket(shoutOut , name) ;
	  
	  
	}
  
  private void storeInS3Bucket(String shoutOut,String name)
  {
	  
  }
  
  

}
