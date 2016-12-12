package com.nj.sender;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Sender
{
	static int w=800,h=600;
	Image image;
	BufferedReader us;
	int pixels[] = new int[w * h];

	public static void main(String str[])
	{
			Sender obj=new Sender();


			obj.us=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Image name:-");
			try
			{
			String st=obj.us.readLine();

			obj.image = Toolkit.getDefaultToolkit().createImage(st);
			obj.new CImage(obj.image);
			}
			catch(Exception e){}
	}

	void incry()
	{
		try
		{

		PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);

	    pg.grabPixels();
	    String s;

	    System.out.println("Enter Message:=");
		s= us.readLine();
		byte b[]=s.getBytes();

//---------------------------- key ---------------------------
		System.out.println("Enter User Name:=");
		String u=us.readLine();

		System.out.println("Enter Password:=");
		String p=us.readLine();

		int key=u.hashCode() ^ p.hashCode();

//---------------------------- Encode message --------------------------------
		int no=b.length;
		System.out.println(no);
		for (int j=0,i=0,m=0;i<w*h && j<no ; i=i+50)
		{

			int alpha=0xff & pixels[i]>>24;
			int red = 0xff & (byte)(b[j++]^key);
			int green=0xff & pixels[i]>>8;
			int blue= 0xff & pixels[i];
	       	pixels[i] = (alpha<<24 | red << 16 | green << 8 | blue);
		}

	    }
	    catch (Exception e)
	    {
			System.out.println(e.toString());
		}
	}

	void send()
	{
		//------------------------- Send File -----------------------------------
			try
			{
				byte buffer[]=new byte[w*h*4];
				for(int i=0,j=0;i<w*h;i++)
				{
					buffer[j++]=(byte)(pixels[i]>>24);
					buffer[j++]=(byte)(pixels[i]>>16);
					buffer[j++]=(byte)(pixels[i]>>8);
					buffer[j++]=(byte)(pixels[i]);
				}

				Socket client=new Socket("localhost",6789);
				client.setSendBufferSize(w*h*4);
				DataOutputStream toserver=new DataOutputStream(client.getOutputStream());
				toserver.write(buffer,0,buffer.length);
				System.out.println(toserver.size());
				client.close();
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}


		//------------------------------------------------------------------------
	}



class CImage extends JFrame implements ActionListener
{
        Image im;

        JButton bsend,bencode;
        JTextField tmsg,timg,tusr,tpwd;
        JLabel lmsg,limg,lusr,lpwd;

       public CImage(Image st)
       {
		   	im=st;
			getContentPane().setLayout(null);
			setTitle("Sender");
			setSize(w,h+100);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().setBackground(new Color(100,100,200));

			bsend=new JButton("Send");
			bsend.setBounds(w/2-100,10,80,30);
			bsend.setEnabled(false);
			bsend.addActionListener(this);

			bencode=new JButton("encode");
			bencode.setBounds(w/2-10,10,80,30);
			bencode.addActionListener(this);

			getContentPane().add(bsend);
			getContentPane().add(bencode);

			setVisible(true);
			//repaint(0,100,w,h);
		}

        public void paint(Graphics g)
        {
			g.drawImage(im,0,100,w,h, this);
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource()==bsend)
			{
				send();
				bsend.setEnabled(false);
				bencode.setEnabled(true);
			}
			else if(ae.getSource()==bencode)
			{
				incry();
				bsend.setEnabled(true);
				bencode.setEnabled(false);
			}
		}
}
}



