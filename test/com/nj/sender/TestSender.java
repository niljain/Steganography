package com.nj.sender;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;

import org.junit.Test;

/**
 * Encode message into image and transmit to receiver (P2P).
 * @author Nilesh
 *
 */
public class TestSender
{
	@Test
	public void testSender()
	{
			Sender obj=new Sender();


			BufferedReader us=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Image name:-");
			try
			{
			String st=us.readLine();

			Image image = Toolkit.getDefaultToolkit().createImage(st);
			obj.new CImage(image);
			}
			catch(Exception e){}
	}
}



