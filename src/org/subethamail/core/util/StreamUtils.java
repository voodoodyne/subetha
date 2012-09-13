package org.subethamail.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Random stream utilities
 */
public class StreamUtils
{
	/** Dump stream to file */
	public static void dumpStream(InputStream in, String file)
	{
		try
		{
			File f = new File(file);
			
			OutputStream os = new FileOutputStream(f);
			int ch;
			while ((ch = in.read()) >= 0)
				os.write(ch);
			
			os.flush();
			os.close();
		}
		catch (IOException ex) { throw new RuntimeException(ex); }
	}
}