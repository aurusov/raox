package ru.bmstu.rk9.rao.ui.process.seize;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;

import ru.bmstu.rk9.rao.ui.process.ProcessConnectionAnchor;
import ru.bmstu.rk9.rao.ui.process.ProcessFigure;

public class SeizeFigure extends ProcessFigure {

	public SeizeFigure() {
		super();

		ProcessConnectionAnchor inputConnectionAnchor, outputConnectionAnchor;
		inputConnectionAnchor = new ProcessConnectionAnchor(this);
		inputConnectionAnchor.offsetHorizontal = 3 * offset;
		inputConnectionAnchor.offsetVertical = 35;
		inputConnectionAnchors.add(inputConnectionAnchor);
		connectionAnchors.put(Seize.TERMINAL_IN, inputConnectionAnchor);

		outputConnectionAnchor = new ProcessConnectionAnchor(this);
		outputConnectionAnchor.offsetHorizontal = 45;
		outputConnectionAnchor.offsetVertical = 35;
		outputConnectionAnchors.add(outputConnectionAnchor);
		connectionAnchors.put(Seize.TERMINAL_OUT, outputConnectionAnchor);

		label.setText(Seize.name);
	}

	@Override
	protected void drawShape(Graphics graphics) {
		Rectangle rectangle = getBounds().getCopy();
		Path path = new Path(null);
		path.addArc(rectangle.x + offset, rectangle.y + 3 * offset, rectangle.width - 2 * offset,
				rectangle.height - 4 * offset, 240, 240);
		PathData data = path.getPathData();
		float[] points = data.points;
		float xStart = points[0];
		float yStart = points[1];
		path.lineTo(xStart, yStart);
		graphics.fillPath(path);
		path.close();
	}
}
