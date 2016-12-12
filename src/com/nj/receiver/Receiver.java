package com.nj.receiver;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.*;

public class Receiver
{
final int w=800,h=600;
    public static void main(String st[])
	{
			Receiver obj=new Receiver();
			obj.decry();
	}

	public void decry()
    {
	   	int p[] = new int[w*h];
		byte buffer[]=new byte[w*h*4];
	try
	{
//------------------------------ Receive Image ---------------------------------------
		ServerSocket welcome=new ServerSocket(6789);
		while(true)
		{
		Socket client=welcome.accept();
		client.setReceiveBufferSize(w*h*4);
		BufferedInputStream user=new BufferedInputStream(client.getInputStream(),w*h*4);
		user.read(buffer,0,w*h*4);
		System.out.println(buffer.length);
		break;
		}

//----------------------------- Create Pixels --------------------------------------
		for(int i=0,j=0;i<w*h;i++)
		{
		p[i]=((0x000000ff & buffer[j++])<<24 | (0x000000ff & buffer[j++])<<16 | (0x000000ff & buffer[j++])<<8 | (0x000000ff & buffer[j++]));
		}

//----------------------------- Draw Image ------------------------------------

MemoryImageSource source = new MemoryImageSource(w,h, p, 0,w);
new CImage(Toolkit.getDefaultToolkit().createImage(source));

BufferedImage thumbImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
Graphics2D graphics2D =thumbImage.createGraphics();

graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
graphics2D.drawImage(Toolkit.getDefaultToolkit().createImage(source), 0, 0,w, h, null);

BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("screencapture.jpg"));

ImageIO.write(thumbImage, "jpg", out);


//-------------------------------- key -------------------------------
		BufferedReader isr=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter User Name:=");
		String us=isr.readLine();

		System.out.println("Enter Password:=");
		String pa=isr.readLine();

		int key=us.hashCode() ^ pa.hashCode();
		isr.close();
//-------------------- Find Message -----------------------------
		byte msg[]=new byte[1024];
		for (int j=0,i=0;i<w*h && j<50; i=i+50)
		{
				if((byte)(0xff & p[i]>>16) < 0)
					break;
				msg[j++]=(byte)((0xff & p[i]>>16)^key);
		}
		System.out.println(new String(msg));
//---------------------------------------------------------------
	}
    catch (Exception e)
    {
		System.out.println(e);
	}
	}

//------------------------- Create Frame ------------------------------
private class CImage extends Frame
{
        Image im;
        public CImage(Image st)
        {
			setTitle("Receiver");
			im=st;
			pack();
			addWindowListener(new Clo());
            setSize(w,h);
            show();
		}
        public void paint(Graphics g)
        {
			g.drawImage(im, 0, 0, this);
        }
        public class Clo extends WindowAdapter
		{
			public void windowClosing(WindowEvent we)
			{
			System.exit(0);
			}
		}
}
}
