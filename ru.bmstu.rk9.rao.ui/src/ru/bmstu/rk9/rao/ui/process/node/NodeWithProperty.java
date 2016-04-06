package ru.bmstu.rk9.rao.ui.process.node;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.IPropertySource;

import ru.bmstu.rk9.rao.ui.process.link.Link;

public abstract class NodeWithProperty extends NodeWithLinks implements IAdaptable {

	private static final long serialVersionUID = 1;

	public static final String PROPERTY_COLOR = "NodeColor";
	public static final String PROPERTY_NAME = "ShowNodeName";

	private boolean nameIsVisible = true;
	private transient IPropertySource propertySource = null;

	public NodeWithProperty(RGB foregroundColor) {
		super(foregroundColor);
		this.sourceConnections = new ArrayList<Link>();
		this.targetConnections = new ArrayList<Link>();
	}

	public void setColor(RGB newColor) {
		RGB oldColor = this.color;
		this.color = newColor;
		getListeners().firePropertyChange(PROPERTY_COLOR, oldColor, newColor);
	}

	public boolean nameIsVisible() {
		return nameIsVisible;
	}

	public void setNameVisible(boolean visible) {
		boolean oldVisible = this.nameIsVisible;
		this.nameIsVisible = visible;
		getListeners().firePropertyChange(PROPERTY_NAME, oldVisible, visible);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			if (propertySource == null) {
				propertySource = new NodePropertySource(this);
			}
			return propertySource;
		}
		return null;
	}
}
