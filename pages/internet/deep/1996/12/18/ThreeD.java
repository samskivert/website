import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Event;
import java.util.StringTokenizer;

//
//
// ThreeD.java
//
// much of this code is based on the sample 3D code supplied with
// the JDK.
//
/** The representation of a 3D model */
class Model3D {
	Vertex[] verts;
	Face[] faces;

    Matrix3D mat;

    float xmin, xmax, ymin, ymax, zmin, zmax;

    Model3D () {
		mat = new Matrix3D ();
    }

    /** Create a 3D model by parsing an input stream */
    Model3D (String modelparams) {
		this();

		faces = new Face[0];
		verts = new Vertex[0];

		StringTokenizer st = new StringTokenizer(modelparams);
		String next = null; //primitive put-back.
		while (st.hasMoreTokens()) {
			String toke;
			if (next != null) {
				toke = next;
				next = null;
			} else {
				 toke = st.nextToken();
			}
			char kind = toke.toLowerCase().charAt(0);
			switch (kind) {
				case 'v': //vertex
					addVert(new Float(st.nextToken()).floatValue(),
							new Float(st.nextToken()).floatValue(),
							new Float(st.nextToken()).floatValue());
					break;

				case 'f': //face
					String faceparams = st.nextToken(); //grabith the color
					while (st.hasMoreTokens()) {
						String t = st.nextToken();
						if (Character.isDigit(t.charAt(0))) {
							faceparams += " " + t;
						} else {
							next = t;
							break;
						}
					}
					addFace(faceparams);
					break;

				default:
					System.out.println("Wacked out token in parameters: " +
										toke);
					break;
			}
		}
		System.out.print("model created with " + verts.length);
		System.out.println(" vertices and " + faces.length + " faces");
	}

	//add a face to this model.
	void addFace(String faceparams) {

		//this is less than optimal, but we don't care because it only
		//happens on startup
		Face[] newfaces = new Face[faces.length + 1];
		System.arraycopy(faces, 0, newfaces, 0, faces.length);
		newfaces[faces.length] = new Face(faceparams, verts);
		faces = newfaces;
	}

	//add a vertex.
    void addVert(float x, float y, float z) {

		Vertex[] newvert = new Vertex[verts.length + 1];
		System.arraycopy(verts, 0, newvert, 0, verts.length);
		newvert[verts.length] = new Vertex(x, y, z);
		verts = newvert;
	}

    /**
	 * paint
	 * Paint this model to a graphics context.  It uses the matrix associated
	 * with this model to map from model space to screen space.
	 */
    void paint(Graphics g) {

		mat.transform(verts);

		//preRender lets all the faces know that the vertices have
		//been transformed, and to calculate their screen Z values.
		for (int ii=0; ii < faces.length; ii++) {
			faces[ii].preRender();
		}

		//sort the faces.
	 	QuickSort.sort(faces);

		//render them.
		for (int ii=0; ii < faces.length; ii++) {
			faces[ii].render(g); 
		}
	}

    /** Find the bounding box of this model */
    void findBB() {
		float xmin, xmax, ymin, ymax, zmin, zmax;
		xmin = ymin = zmin = Float.MAX_VALUE;
		xmax = ymax = zmax = Float.MIN_VALUE;

		for (int ii = 0; ii < verts.length; ii++) {
			float x = verts[ii].x;
			if (x < xmin) {
				xmin = x;
			}
			if (x > xmax) {
				xmax = x;
			}
			float y = verts[ii].y;
			if (y < ymin) {
				ymin = y;
			}
			if (y > ymax) {
				ymax = y;
			}
			float z = verts[ii].z;
			if (z < zmin) {
				zmin = z;
			}
			if (z > zmax) {
				zmax = z;
			}
		}
		this.xmax = xmax;
		this.xmin = xmin;
		this.ymax = ymax;
		this.ymin = ymin;
		this.zmax = zmax;
		this.zmin = zmin;
    }
}

/** An applet to put a 3D model into a page */
public class ThreeD extends Applet {

    Model3D md;

    float xfac;
    int prevx, prevy;
    float xtheta, ytheta;
    float scalefudge = 1;

	// amat is our cumulative rotation matrix, and
	// tmat is the amount we rotate each time the mouse is dragged over
	// the model.
    Matrix3D amat = new Matrix3D(), tmat = new Matrix3D();

	//width and height of the applet.
	int w, h;

    public void init() {
		try {
			scalefudge = Float.valueOf(getParameter("scale")).floatValue();
		} catch(Exception e){};


		//give the model a bit of an initial rotation, so that it looks
		//interesting when it comes up.
		amat.yrot(20);
		amat.xrot(20);

		String modelparam = getParameter("model");
		if (modelparam == null) {
			System.out.println("model param missing! Cannot run.");
			return;
		}
		md = new Model3D(modelparam);

		//find the bounding box of the model.
		md.findBB();

		//find the extents of the model and set xw to the largest one
		float xw = md.xmax - md.xmin;
		float yw = md.ymax - md.ymin;
		float zw = md.zmax - md.zmin;
		if (yw > xw) {
			xw = yw;
		}
		if (zw > xw) {
			xw = zw;
		}

		float f1 = size().width / xw;
		float f2 = size().height / xw;
		xfac = 0.7f * (f1 < f2 ? f1 : f2) * scalefudge;

		Dimension d = size();
		w = d.width;
		h = d.height;
		repaint();
    }

	/**
	 * mouseDown
	 */
    public boolean mouseDown(Event e, int x, int y) {
		prevx = x;
		prevy = y;

		repaint();

		return true;
    }

	/**
	 * mouseDrag
	 * 
	 * rotate the model as they drag the mouse over it.
	 */
    public boolean mouseDrag(Event e, int x, int y) {

		//tmat is our matrix for this tiny bit of rotation.
		tmat.unit();
		float xtheta = (prevy - y) * 360.0f / w;
		float ytheta = (x - prevx) * 360.0f / h;
		tmat.xrot(xtheta);
		tmat.yrot(ytheta);
		
		//multiply it into amat, our cumulative rotation matrix.
		amat.mult(tmat);

		repaint();

		prevx = x;
		prevy = y;
		return true;
    }

	Graphics offG = null;
	Image offImg = null;
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * paint
	 *
	 * paint the model.
	 */
    public void paint(Graphics g) {
		if (md != null) {

			//reset the transformation matrix of the model.
			md.mat.unit();

			//translate the model to the origin, so that the center of
			// it lies right on the origin.
			//without this our rotations will be about the origin,
			//which is bad if the model is nowhere near it.
			md.mat.translate(-(md.xmin + md.xmax) / 2,
							-(md.ymin + md.ymax) / 2,
							-(md.zmin + md.zmax) / 2);

			//multiply the model by the rotation matrix
			md.mat.mult(amat);

			//expand the size of the model so that it is close
			//to the size of the window
			md.mat.scale(xfac, -xfac, 16 * xfac / w);

			//translate it away from the origin so that we can see it.
			md.mat.translate(w / 2, h / 2, 8);

			//we double buffer the image production for smooth animation.
			if (offImg == null) {
				offImg = createImage(w, h);
				offG = offImg.getGraphics();
			}

			//clear out the background.
			offG.setColor(Color.white);
			offG.fillRect(0, 0, w, h);

			//paint the model on top of it.
			md.paint(offG);

			//draw the offscreen graphics to the real screen.
			g.drawImage(offImg, 0, 0, null);

		} else {
			g.drawString("Error in model: not painting.", 3, 20);
		}
    }
}
