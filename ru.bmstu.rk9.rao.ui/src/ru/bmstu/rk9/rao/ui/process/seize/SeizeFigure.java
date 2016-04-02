package ru.bmstu.rk9.rao.ui.process.seize;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Path;

import ru.bmstu.rk9.rao.ui.process.ProcessConnectionAnchor;
import ru.bmstu.rk9.rao.ui.process.ProcessFigure;

public class SeizeFigure extends ProcessFigure {

	class Shape extends Figure {
		@Override
		final protected void paintFigure(Graphics graphics) {
			Path path = new Path(null);
			addArcToPath(path);
			final float[] points = path.getPathData().points;
			final float xStart = points[0];
			final float yStart = points[1];
			path.lineTo(xStart, yStart);
			path.close();

			graphics.setBackgroundColor(getBackgroundColor());
			graphics.fillPath(path);
		}
	}

	private Shape shape = new Shape();

	@Override
	public IFigure getShape() {
		return shape;
	}

	public SeizeFigure() {
		super();

		add(shape);

		ProcessConnectionAnchor inputConnectionAnchor = new ProcessConnectionAnchor(shape);
		inputConnectionAnchors.add(inputConnectionAnchor);
		connectionAnchors.put(Seize.TERMINAL_IN, inputConnectionAnchor);

		ProcessConnectionAnchor outputConnectionAnchor = new ProcessConnectionAnchor(shape);
		outputConnectionAnchors.add(outputConnectionAnchor);
		connectionAnchors.put(Seize.TERMINAL_OUT, outputConnectionAnchor);

		shape.addFigureListener(new FigureListener() {
			@Override
			public void figureMoved(IFigure shape) {
				Path path = new Path(null);
				addArcToPath(path);
				path.close();
				if (path.getPathData().points.length == 0)
					return;
				final float xStart = path.getPathData().points[0];

				Rectangle bounds = shape.getBounds();
				inputConnectionAnchor.setOffsetHorizontal((int) xStart - bounds.x);
				inputConnectionAnchor.setOffsetVertical(bounds.height / 2);

				outputConnectionAnchor.setOffsetHorizontal(bounds.width);
				outputConnectionAnchor.setOffsetVertical(bounds.height / 2);
			}
		});

		label.setText(Seize.name);
	}

	private final void addArcToPath(Path path) {
		Rectangle bounds = getShape().getBounds();
		path.addArc(bounds.x, bounds.y, bounds.width, bounds.height, 240, 240);
	}
}
