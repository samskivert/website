//
//
// $Id$
// Face
//
// a class representing a polygon face on a 3d model.

import java.util.StringTokenizer;
import java.awt.Color;
import java.awt.Graphics;

class Face implements Sortable {

	// the vertices that make up this face.
	private Vertex[]	verts;

	//the color to render this face in.
	private Color		color;

	/* set up in prerender */ 
	private	int[]		xs;
	private int[]		ys;
	private float		z;

	public Face(String faceparams, Vertex[] vertices) {
		StringTokenizer st = new StringTokenizer(faceparams);

		System.out.println("creating face with |" + faceparams + "|");
		//parse color as a base 16 number. eg. "663300"
		color = new Color(Integer.parseInt(st.nextToken(), 16));
		
		//read in and set up the veritices of this face
		verts = new Vertex[st.countTokens()];
		xs = new int[verts.length];
		ys = new int[verts.length];

		for (int ii=0; ii < verts.length; ii++) {
			verts[ii] = vertices[Integer.parseInt(st.nextToken())];
		}
	}

	// here we have to figure out the Z anyway, so we also 
	// prepare the x's and y's for rendering.
	// in this simple applet, we sort faces by their average Z
	// value. This is super primitive, but it works for things like
	// cubes.
	public void preRender() {
		z = 0;
		for (int ii=0; ii < verts.length; ii++) {
			xs[ii] = verts[ii].screenX;
			ys[ii] = verts[ii].screenY;
			z += verts[ii].screenZ;
		}
		z /= verts.length;
	}

	//do the painting thing.
	public void render(Graphics g) {
		g.setColor(color);
		g.fillPolygon(xs, ys, xs.length);
	}

	//Sortable interface method
	public boolean lessThan(Sortable other) {
		return (z < ((Face) other).z);
	}
}
