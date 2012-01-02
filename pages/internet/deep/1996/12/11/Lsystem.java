//
// Lsystem - A not so efficient applet to compute L-system fractals with code
//           to handle context sensitive L-systems and macro substitution in
//           the productions to make things a little easier to type. do make
//           note of the last checkin time below and compare that to the time
//           the article went up before you make comments on the elegance of
//           this code. :)
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1996/12/11/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

public class Lsystem extends DBApplet
{
    //
    // Lsystem public member functions

    public void init ()
    {
        super.init();

        _maxIters = Integer.valueOf(getParameter("maxIters")).intValue();
        _angle = Float.valueOf(getParameter("angle")).floatValue();
        _angle = _angle / 360.0f * 2.0f * (float)Math.PI;
        _startAngle = Float.valueOf(getParameter("startAngle")).floatValue();
        _startAngle = _startAngle / 360.0f * 2.0f * (float)Math.PI;

        _axiom = getParameter("axiom");

        String lhs, rhs;

        for (int i = 0;
             (lhs = getParameter("macro" + i + ".lhs")) != null; i++) {
            rhs = getParameter("macro" + i + ".rhs");
            _macros.put(lhs, rhs);
        }

        for (int i = 0;
             (lhs = getParameter("production" + i + ".lhs")) != null; i++) {
            rhs = getParameter("production" + i + ".rhs");
            _productions.put(lhs, rhs);
        }
    }

    // iterate through string substituting all production left hand sides seen
    // in the string for their right hand sides. you might note that the
    // production left hand sides have to be one character in length, but
    // that's why we provide macro substitution. :)
    String applyProductions (String s)
    {
        for (int i = 0; i < s.length(); i++) {
            String rhs = (String)_productions.get(String.valueOf(s.charAt(i)));
            if (rhs != null) {
                return s.substring(0, i) + rhs +
                    applyProductions(s.substring(i+1, s.length()));
            }
        }
        return s;
    }

    // substitute all occurances of string lhs for string rhs
    String sub (String s, String lhs, String rhs)
    {
        int lhsi = s.indexOf(lhs);
        if (lhsi != -1) {
            return s.substring(0, lhsi) + rhs +
                sub(s.substring(lhsi + lhs.length(), s.length()), lhs, rhs);
        }
        return s;
    }

    String applyMacros (String s)
    {
        Enumeration e = _macros.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            s = sub(s, key, (String)_macros.get(key));
        }
        return s;
    }

    public void run ()
    {
        while (_running) {
            String axiom = _axiom;
            String program = applyMacros(axiom);
            int i = 0;

            blank();
            message(i + " iters", "Click");
            _renderScale = computeRenderScale(program);
            render(program);
            waitForClick();

            for (i = 1; i < _maxIters; i++) {

                axiom = applyProductions(axiom);
                program = applyMacros(axiom);

                blank();
                message(i + " iters", "Click");
                _renderScale = computeRenderScale(program);
                render(program);

                waitForClick();
            }
        }
    }

    public float computeRenderScale (String program)
    {
        float x = 0.0f, y = 0.0f;
        float maxX = 0.0f, maxY = 0.0f, minX = 0.0f, minY = 0.0f;

        // check max and min dimensions
        if (x > maxX) maxX = x;
        else if (x < minX) minX = x;
        if (y > maxY) maxY = y;
        else if (y < minY) minY = y;

        _currentAngle = _startAngle;

        for (int i = 0; i < program.length(); i++) {
            switch (program.charAt(i)) {

            case 'F':
            case 'f':
                // move along
                x += (float)Math.cos(_currentAngle);
                y += (float)Math.sin(_currentAngle);
                // check max and min dimensions
                if (x > maxX) maxX = x;
                else if (x < minX) minX = x;
                if (y > maxY) maxY = y;
                else if (y < minY) minY = y;
                break;

            case '+':
                _currentAngle -= _angle;
                break;

            case '-':
                _currentAngle += _angle;
                break;

            case '[':
                _xs.push(new Float(x));
                _ys.push(new Float(y));
                _as.push(new Float(_currentAngle));
                break;

            case ']': {
                x = ((Float)_xs.pop()).floatValue();
                y = ((Float)_ys.pop()).floatValue();
                _currentAngle = ((Float)_as.pop()).floatValue();
                break;
            }

            default:
                break;
            }
        }

        float width = maxX - minX;
        float height = maxY - minY;
        float scale;

        Dimension d = size();
        int dim = Math.min(d.width, d.height);

        if (width/height > (float)d.width/(float)d.height) {
            scale = ((float)d.width-10)/width;
        } else {
            scale = ((float)d.height-10)/height;
        }

        int pixw = Math.round(width*scale);
        int pixh = Math.round(height*scale);

        _xroff = (d.width - pixw)/2 - Math.round(minX*scale);
        _yroff = (d.height - pixh)/2 - Math.round(minY*scale);

        return scale;
    }

    public void render (String program)
    {
        float x = 0.0f, y = 0.0f, nx, ny;

        _currentAngle = _startAngle;

        for (int i = 0; i < program.length(); i++) {
            switch (program.charAt(i)) {

            case 'F':
                nx = x + (float)Math.cos(_currentAngle);
                ny = y + (float)Math.sin(_currentAngle);

                _offGraphics.drawLine(_xroff + Math.round(x*_renderScale),
                                      _yroff + Math.round(y*_renderScale),
                                      _xroff + Math.round(nx*_renderScale),
                                      _yroff + Math.round(ny*_renderScale));
                repaint();
                Thread.yield();

                x = nx;
                y = ny;
                break;

            case 'f':
                x += Math.cos(_angle);
                y += Math.sin(_angle);
                break;

            case '+':
                _currentAngle -= _angle;
                break;

            case '-':
                _currentAngle += _angle;
                break;

            case '[':
                _xs.push(new Float(x));
                _ys.push(new Float(y));
                _as.push(new Float(_currentAngle));
                break;

            case ']': {
                x = ((Float)_xs.pop()).floatValue();
                y = ((Float)_ys.pop()).floatValue();
                _currentAngle = ((Float)_as.pop()).floatValue();
                break;
            }

            default:
                break;
            }
        }
    }

    public void blank ()
    {
        Dimension d = size();
        // clear out the background
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(0, 0, d.width, d.height);
        _offGraphics.setColor(Color.black);
        title("L-system", "Fractal");
    }

    //
    // Lsystem protected data members

    float _renderScale;

    float _angle;
    float _startAngle;
    float _currentAngle;

    String _axiom;

    int _xroff;
    int _yroff;
    int _maxIters;

    Hashtable _macros = new Hashtable();
    Hashtable _productions = new Hashtable();

    Stack _xs = new Stack();
    Stack _ys = new Stack();
    Stack _as = new Stack();
};
