package com.nj.receiver;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Receiver {
	final int w = 800, h = 600;

	public void decry() {
		int p[] = new int[w * h];
		byte buffer[] = new byte[w * h * 4];

		try {
			// ------- Receive Image ------------------
			ServerSocket welcome = new ServerSocket(6789);
			while (true) {
				Socket client = welcome.accept();
				client.setReceiveBufferSize(w * h * 4);
				BufferedInputStream user = new BufferedInputStream(client.getInputStream(), w * h * 4);
				user.read(buffer, 0, w * h * 4);
				System.out.println(buffer.length);
				break;
			}

			// ------------ Create Pixels --------------------------------------
			for (int i = 0, j = 0; i < w * h; i++) {
				p[i] = ((0x000000ff & buffer[j++]) << 24 | (0x000000ff & buffer[j++]) << 16
				        | (0x000000ff & buffer[j++]) << 8 | (0x000000ff & buffer[j++]));
			}

			// ------------- Draw Image-----------------------------

			MemoryImageSource source = new MemoryImageSource(w, h, p, 0, w);
			new CImage(Toolkit.getDefaultToolkit().createImage(source));

			BufferedImage thumbImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = thumbImage.createGraphics();

			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(Toolkit.getDefaultToolkit().createImage(source), 0, 0, w, h, null);

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("screencapture.jpg"));

			ImageIO.write(thumbImage, "jpg", out);

			// -------------------------------- key
			// -------------------------------
			BufferedReader isr = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter User Name:=");
			String us = isr.readLine();

			System.out.println("Enter Password:=");
			String pa = isr.readLine();

			int key = us.hashCode() ^ pa.hashCode();
			isr.close();
			// -------------------- Find Message -----------------------------
			byte msg[] = new byte[1024];
			for (int j = 0, i = 0; i < w * h && j < 50; i = i + 50) {
				if ((byte) (0xff & p[i] >> 16) < 0)
					break;
				msg[j++] = (byte) ((0xff & p[i] >> 16) ^ key);
			}
			System.out.println(new String(msg));
			// ---------------------------------------------------------------
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// ------------------------- Create Frame ------------------------------
	private class CImage extends Frame {
		Image im;

		public CImage(Image st) {
			setTitle("Receiver");
			im = st;
			pack();
			addWindowListener(new Clo());
			setSize(w, h);
			show();
		}

		@Override
		public void paint(Graphics g) {
			g.drawImage(im, 0, 0, this);
		}

		public class Clo extends WindowAdapter {
			@Override
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		}
	}
}
