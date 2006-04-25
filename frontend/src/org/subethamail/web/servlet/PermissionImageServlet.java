/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.servlet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Permission;
import org.subethamail.web.action.GetMessage;

/**
 * Servlet generates and returns a reasonably nice PNG image
 * that describes the permission.  This is needed so that we can
 * display vertical text.  Unfortunately there is not yet any
 * web-standard way of doing so.
 * 
 * There are only a few permissions so we cache the PNG images
 * in memory.
 * 
 * Parameters expected:
 * 
 * perm: the permission name
 */
@SuppressWarnings("serial")
public class PermissionImageServlet extends HttpServlet
{
	/** */
	private static Log log = LogFactory.getLog(GetMessage.class);

	/** */
	public static final String PERMISSION_PARAM = "perm";
	
	/** TODO:  figure out the img size dynamically */
	public static final int IMG_WIDTH = 35;
	public static final int IMG_HEIGHT = 140;
	
	/** */
	public static final double TEXT_ANGLE_RADIANS = -Math.PI/2.2;
	
	/**
	 */
	protected Map<Permission, ByteArrayOutputStream> imageCache =
		new ConcurrentHashMap<Permission, ByteArrayOutputStream>();
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Servicing " + request.getRequestURI());

		String permString = request.getParameter(PERMISSION_PARAM);
		Permission perm = Permission.valueOf(permString);
		
		ByteArrayOutputStream cachedStream = this.imageCache.get(perm);
		if (cachedStream == null)
		{
			if (log.isInfoEnabled())
				log.info("Cache miss; building image for " + perm);
			
			Image img = this.buildImage(perm);

			cachedStream = new ByteArrayOutputStream(2048);	// is 2k enough?

			ImageIO.write((RenderedImage)img, "png", cachedStream);
			
			this.imageCache.put(perm, cachedStream);
		}

		// And send back the results!
		response.setContentType("image/png");
		response.setHeader("Cache-Control", "public, max-age=360000");

		cachedStream.writeTo(response.getOutputStream());
	}
	
	/**
	 * Build the permission image.
	 */
	protected Image buildImage(Permission perm) throws ServletException, IOException
	{
		Graphics2D g = null;

		try
		{
			Image img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);

			g = (Graphics2D)img.getGraphics();

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

			// TODO:  why doesn't this work?
			// Lay down the background as transparent (or at least try to)
			//g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			
			// Just white background instead
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
			
			// Now draw text at a diagonal angle from bottom left to top right.
			// TODO:  remove the black magic constants
			g.setColor(Color.BLACK);
			g.translate(IMG_WIDTH-25, IMG_HEIGHT);
			g.rotate(TEXT_ANGLE_RADIANS);
			g.drawString(perm.getPretty(), 0, 0);
			
			return img;
		}
		finally
		{
			if (g != null)
				g.dispose();
		}
	}
}
