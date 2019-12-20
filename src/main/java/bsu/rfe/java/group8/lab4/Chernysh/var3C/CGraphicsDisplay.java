package bsu.rfe.java.group8.lab4.Chernysh.var3C;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EmptyStackException;
import java.util.Stack;

public class CGraphicsDisplay extends JPanel {
    class Zone {
	double dTmp;
	double dMinY;
        double dMaxY;
	double dMaxX;
	double dMinX;
	boolean bUse;
    }
        
    class Graph {
        private double dX;
        private double dY;
        private int iX;
        private int iY;
        private int iNumb;
    }
        
    private Graph graphPoint;    
    
    private Double[][] dArrGraphicsData;
    private Double[][] dArrSecondGraphicsData;
    private int[][] iArrGraphicsData;
    private boolean bShowAxis = true;
    private boolean bShowMarkers = true;
    private boolean bClockRotate = false;
    private boolean bOneMoreGraph = false;
    private boolean bSelMode = false;
    private boolean bDragMode = false;
    private boolean bZoom = false;
    private double dMinX;
    private double dMaxX;
    private double dMinY;
    private double dMaxY;
    private double dScale;
    private double dScaleX;
    private double dScaleY;
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke selStroke;
    private Font axisFont;
    private int iMausePX = 0;
    private int iMausePY = 0;
    private Rectangle2D.Double rect;
    private Zone zone = new Zone();
    private Stack<Zone> stack = new Stack<Zone>();

    CGraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(5.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[] {6,2,6,2,6,2,2,2,2,2,2,2}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
	selStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                        BasicStroke.JOIN_MITER, 10.0f, new float[] { 8, 8 }, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
    }
    
    void showGraphics(Double[][] graphicsData) {
        this.dArrGraphicsData = graphicsData;
        iArrGraphicsData = new int[graphicsData.length][2];
        repaint();
    }
    
    void showGraphics(Double[][] graphicsData, Double[][] graphicsSecondData, boolean secondGraph) {
        this.dArrGraphicsData = graphicsData;
        this.dArrSecondGraphicsData = graphicsSecondData;
        this.bOneMoreGraph = secondGraph;
        iArrGraphicsData = new int[graphicsData.length + dArrSecondGraphicsData.length][2];
        repaint();
    }
        
    void setShowAxis(boolean showAxis) {
        this.bShowAxis = showAxis;
        repaint();
    }

    void setShowMarkers(boolean showMarkers) {
        this.bShowMarkers = showMarkers;
        repaint();
    }

    void setClockRotate(boolean clockRotate) {
        this.bClockRotate = clockRotate;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dArrGraphicsData == null || dArrGraphicsData.length == 0)
            return;
        dMinX = dArrGraphicsData[0][0];
        dMaxX = dArrGraphicsData[dArrGraphicsData.length - 1][0];
        dMinY = dArrGraphicsData[0][1];
        dMaxY = dMinY;

        for (int i = 1; i < dArrGraphicsData.length; i++) {
            if (dArrGraphicsData[i][1] < dMinY) {
                dMinY = dArrGraphicsData[i][1];
            }
            if (dArrGraphicsData[i][1] > dMaxY) {
                dMaxY = dArrGraphicsData[i][1];
            }
        }
        if (bOneMoreGraph) {
            for (int i = 1; i < dArrSecondGraphicsData.length; i++) {
                if (dArrSecondGraphicsData[i][1] < dMinY) {
                    dMinY = dArrSecondGraphicsData[i][1];
                }
                if (dArrSecondGraphicsData[i][1] > dMaxY) {
                    dMaxY = dArrSecondGraphicsData[i][1];
                }
            }
        }

        if (!bClockRotate) {
            dScaleX = getSize().getWidth() / (dMaxX - dMinX);
            dScaleY = getSize().getHeight() / (dMaxY - dMinY);
        }
        else {
            dScaleX = getSize().getHeight() / (dMaxX - dMinX);
            dScaleY = getSize().getWidth() / (dMaxY - dMinY);
        }
        dScale = Math.min(dScaleX, dScaleY);
        if (dScale == dScaleX) {
            double yIncrement = 0;
            if (!bClockRotate) {
                yIncrement = (getSize().getWidth() / dScale - (dMaxY - dScale)) / 2;
            }
            else {
                yIncrement = (getSize().getHeight() / dScale - (dMaxY - dScale)) / 2;
            }
            dMaxY += yIncrement;
            dMinY -= yIncrement;
        }
        if (dScale == dScaleY) {
            double xIncrement = 0;
            if (!bClockRotate) {
                xIncrement = (getSize().getWidth() / dScale - (dMaxX - dMinX)) / 2;
            }
            else {
                xIncrement = (getSize().getHeight() / dScale - (dMaxX - dMinX)) / 2;
            }
            dMaxX += xIncrement;
            dMinX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (bClockRotate) {
            paintRotate(canvas);
        }
        if (bShowAxis)
            paintAxis(canvas);
        if (bOneMoreGraph)
            paintSecondGraphics(canvas);
        if (bShowMarkers)
            paintMarkers(canvas);
	if (bSelMode) {
            canvas.setColor(Color.BLACK);
            canvas.setStroke(selStroke);
            canvas.draw(rect);
	}
                
        paintGraphics(canvas);
        
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }
    
    private void paintSecondGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < dArrSecondGraphicsData.length; i++) {
            Point2D.Double point = xyToPoint(dArrSecondGraphicsData[i][0], dArrSecondGraphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }
    
    private void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < dArrGraphicsData.length; i++) {
            Point2D.Double point = xyToPoint(dArrGraphicsData[i][0], dArrGraphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }
    
    private void paintRotate(Graphics2D canvas) {
        canvas.rotate(-Math.PI / 2);
        canvas.translate(-getHeight(), 0);
    }

    private void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (int i = 1; i < dArrGraphicsData.length; i++) {
            canvas.setStroke(axisStroke);
            if (dArrGraphicsData[i][1] > dArrGraphicsData[i - 1][1]) {
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            } else {
                canvas.setColor(Color.BLACK);
                canvas.setPaint(Color.BLACK);
            }
            Ellipse2D.Double marker = new Ellipse2D.Double();
            Point2D.Double center = xyToPoint(dArrGraphicsData[i][0], dArrGraphicsData[i][1]);
            Point2D.Double corner = shiftPoint(center, 5, 5);
            marker.setFrameFromCenter(center, corner);
            canvas.draw(marker);
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, 5), shiftPoint(center, 0, -5)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 5, 0), shiftPoint(center, -5, 0)));
        }
        if (bOneMoreGraph) {
            for (int i = 1; i < dArrSecondGraphicsData.length; i++) {
                canvas.setStroke(axisStroke);
                if (dArrSecondGraphicsData[i][1] > dArrSecondGraphicsData[i - 1][1]) {
                    canvas.setColor(Color.RED);
                    canvas.setPaint(Color.RED);
                } else {
                    canvas.setColor(Color.BLACK);
                    canvas.setPaint(Color.BLACK);
                }
                Ellipse2D.Double marker = new Ellipse2D.Double();
                Point2D.Double center = xyToPoint(dArrSecondGraphicsData[i][0], dArrSecondGraphicsData[i][1]);
                Point2D.Double corner = shiftPoint(center, 5, 5);
                marker.setFrameFromCenter(center, corner);
                canvas.draw(marker);
                canvas.draw(new Line2D.Double(shiftPoint(center, 0, 5), shiftPoint(center, 0, -5)));
                canvas.draw(new Line2D.Double(shiftPoint(center, 5, 0), shiftPoint(center, -5, 0)));
            }
        }
    }


    private void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);

        FontRenderContext context = canvas.getFontRenderContext();
        if (dMinX <= 0.0 && dMaxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, dMaxY), xyToPoint(0, dMinY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, dMaxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, dMaxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }

        if (dMinY <= 0.0 && dMaxY >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(dMinX, 0), xyToPoint(dMaxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(dMaxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(dMaxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }
    }

    private Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - dMinX;
        double deltaY = dMaxY - y;
        return new Point2D.Double(deltaX * dScale, deltaY * dScale);
    }

    private Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
    
    private Point2D.Double pointToXY(int x, int y) {
	Point2D.Double p = new Point2D.Double();
	if (!bClockRotate) {
            p.x = x / dScale + dMinX;
            int q = (int) xyToPoint(0, 0).y;
            p.y = dMaxY - dMaxY * ((double) y / (double) q);
	} 
        else {
            if (!bZoom) {
		p.y = -x / dScale + dMaxY;
                p.x = -y / dScale + dMaxX;
		}
            else {
		p.y = -x / dScaleY + dMaxY;
		p.x = -y / dScaleX + dMaxX;
            }
	}
	return p;
    }
    
    public class MouseMotionHandler implements MouseMotionListener, MouseListener {
        public void mouseMoved (MouseEvent ev) {
            Graph graph;
            graph = findDot(ev.getX(), ev.getY());
            if (graph != null) {
		setCursor(Cursor.getPredefinedCursor(8));
		graphPoint = graph;
            }
            else {
		setCursor(Cursor.getPredefinedCursor(0));
		graphPoint = null;
            }
            repaint();
        }
        
        public void mouseDragged(MouseEvent ev) {
            if (bSelMode) {
		if (!bClockRotate)
                    rect.setFrame(iMausePX, iMausePY, ev.getX() - rect.getX(), ev.getY() - rect.getY());
		else {
                    rect.setFrame(- iMausePY + getHeight(), iMausePX, -ev.getY() + iMausePY, ev.getX() - iMausePX);
       		}			
            repaint();
            }
            if (bDragMode) {
                if (!bClockRotate) {
                    if (pointToXY(ev.getX(), ev.getY()).y < dMaxY && pointToXY(ev.getX(), ev.getY()).y > dMinY) {
			dArrGraphicsData[graphPoint.iNumb][1] = pointToXY(ev.getX(), ev.getY()).y;
			graphPoint.dY = pointToXY(ev.getX(), ev.getY()).y;
			graphPoint.iY = ev.getY();
                    }
		} 
                else {
                    if (pointToXY(ev.getX(), ev.getY()).y < dMaxY && pointToXY(ev.getX(), ev.getY()).y> dMinY) {
			dArrGraphicsData[graphPoint.iNumb][1] = pointToXY(ev.getX(), ev.getY()).y;
			graphPoint.dY = pointToXY(ev.getX(), ev.getY()).y;
			graphPoint.iY = ev.getX();
                    }
		}
		repaint();
            }
        }
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != 3)	
                return;	
            try {
                zone = stack.pop();	
            } 
            catch (EmptyStackException err) {			
            }
            if (stack.empty())
		bZoom = false;
	repaint();
        }

        public void mousePressed(MouseEvent ev) {
            if (ev.getButton() != 1)
                return;
            if (graphPoint != null) {
		bSelMode = false;
		bDragMode = true;
            } 
            else {
		bDragMode = false;
		bSelMode = true;
		iMausePX = ev.getX();
		iMausePY = ev.getY();
		if (!bClockRotate)
                    rect.setFrame(ev.getX(), ev.getY(), 0, 0);
		else
		rect.setFrame(ev.getX(), ev.getY(), 0, 0);
            }
        }

        public void mouseReleased(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); 
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        private Graph findDot (int x, int y) {
            Graph graph1 = new Graph();
            Graph graph2 = new Graph();
            double r, r2 = 1000;
            for (int i = 0; i < dArrGraphicsData.length; i++) {
		Point p = new Point();
		p.x = x;
		p.y = y;
		Point p2 = new Point();
		p2.x = iArrGraphicsData[i][0];
		p2.y = iArrGraphicsData[i][1];
		r = Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
                if (r < 7.0) {
                    graph1.iX = iArrGraphicsData[i][0];
                    graph1.iY = iArrGraphicsData[i][1];
                    graph1.dX = iArrGraphicsData[i][0];
                    graph1.dY = iArrGraphicsData[i][1];
                    graph1.iNumb = i;
                    if (r < r2) {
                        r2 = r;
                        graph2 = graph1;
                    }
                    return graph2;
                }
            }
            return null;
        }
    }
}
