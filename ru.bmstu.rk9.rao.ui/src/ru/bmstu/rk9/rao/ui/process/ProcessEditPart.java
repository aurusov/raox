package ru.bmstu.rk9.rao.ui.process;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ru.bmstu.rk9.rao.ui.process.connection.Connection;
import ru.bmstu.rk9.rao.ui.process.node.Node;
import ru.bmstu.rk9.rao.ui.process.node.NodeWithConnections;
import ru.bmstu.rk9.rao.ui.process.node.NodeWithProperty;

public abstract class ProcessEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener, NodeEditPart {

	private int ID;

	public void setID(int ID) {
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}

	@Override
	public final List<Node> getModelChildren() {
		return ((Node) getModel()).getChildren();
	}

	@Override
	public void activate() {
		super.activate();
		((Node) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((Node) getModel()).removePropertyChangeListener(this);
	}

	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.showView(IPageLayout.ID_PROP_SHEET);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(NodeWithProperty.PROPERTY_COLOR)) {
			getFigure().setBackgroundColor(new Color(null, (RGB) evt.getNewValue()));
		}
		if (evt.getPropertyName().equals(NodeWithProperty.PROPERTY_NAME)) {
			((ProcessFigure) getFigure()).setFigureNameVisible((boolean) evt.getNewValue());
		}
		if (evt.getPropertyName().equals(Node.PROPERTY_CONSTRAINT))
			refreshVisuals();
		if (evt.getPropertyName().equals(NodeWithConnections.SOURCE_CONNECTION_UPDATED))
			refreshSourceConnections();
		if (evt.getPropertyName().equals(NodeWithConnections.TARGET_CONNECTION_UPDATED))
			refreshTargetConnections();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ProcessDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ConnectionPolicy());
	}

	@Override
	protected void refreshVisuals() {
		if (!(getFigure() instanceof ProcessFigure))
			return;
		ProcessFigure figure = (ProcessFigure) getFigure();
		Node model = (Node) getModel();

		figure.setConstraint(model.getConstraint());
		RGB oldColor = model.getColor();
		Color newColor = new Color(null, oldColor);
		figure.setBackgroundColor(newColor);

		if (model instanceof NodeWithProperty)
			figure.setFigureNameVisible(((NodeWithProperty) model).nameIsVisible());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectionEditPart) {
		Connection connection = (Connection) connectionEditPart.getModel();
		return getProcessFigure().getConnectionAnchor(connection.getSourceDockName());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point point = new Point(((DropRequest) request).getLocation());
		return getProcessFigure().getSourceConnectionAnchorAt(point);
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectionEditPart) {
		Connection connection = (Connection) connectionEditPart.getModel();
		return getProcessFigure().getConnectionAnchor(connection.getTargetDockName());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point point = new Point(((DropRequest) request).getLocation());
		return getProcessFigure().getTargetConnectionAnchorAt(point);
	}

	@Override
	public List<Connection> getModelSourceConnections() {
		return ((NodeWithConnections) getModel()).getSourceConnections();
	}

	@Override
	public List<Connection> getModelTargetConnections() {
		return ((NodeWithConnections) getModel()).getTargetConnections();
	}

	final protected String mapConnectionAnchorToDock(ConnectionAnchor connectionAnchor) {
		return getProcessFigure().getConnectionAnchorName(connectionAnchor);
	}

	protected ProcessFigure getProcessFigure() {
		return (ProcessFigure) getFigure();
	}
}
