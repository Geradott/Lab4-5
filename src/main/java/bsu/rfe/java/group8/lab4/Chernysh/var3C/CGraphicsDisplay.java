package bsu.rfe.java.group8.lab4.Chernysh.var3C;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JPanel;

public class CGraphicsDisplay extends JPanel {
    private Double[][] dArrGraphicsData;
    private boolean bShowAxis = true;
    private boolean bShowMarkers = true;
    private boolean bClockRotate = false;
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
    private Font axisFont;

    CGraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(5.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[] {6,2,6,2,6,2,2,2,2,2,2,2}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    void showGraphics(Double[][] graphicsData) {
        this.dArrGraphicsData = graphicsData;
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
        //float alpha = 1f-(.01f*(float)opcounter);
        //Graphics2D g2d = (Graphics2D)g.create();
        //AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OUT);
        //AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.Clear);
        canvas.setComposite(AlphaComposite.SrcOver);
        //g2d.drawImage(img, 0, 0, null);
        //g2d.dispose();
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (bClockRotate) {
            ((Graphics2D) g).rotate(-Math.PI / 2);
            ((Graphics2D) g).translate(-getHeight(), 0);
            paintRotate(canvas);
        }
        if (bShowAxis)
            paintAxis(canvas);
        paintGraphics(canvas);
        if (bShowMarkers)
            paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
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
    }

    private void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (int i = 1; i < dArrGraphicsData.length; i++) {
            canvas.setStroke(axisStroke);
            if (dArrGraphicsData[i][1] > dArrGraphicsData[i - 1][1]) {
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.BLACK);
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
}
