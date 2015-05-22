package ru.bmstu.rk9.rdo.ui.contributions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import ru.bmstu.rk9.rdo.lib.CollectedDataNode;
import ru.bmstu.rk9.rdo.lib.PlotDataParser;

public class RDOPlotView extends ViewPart {

	public static final String ID = "ru.bmstu.rk9.rdo.ui.RDOPlotView";
	private final static Map<CollectedDataNode, Integer> openedPlotMap = new HashMap<CollectedDataNode, Integer>();

	private RDOChartComposite frame;
	private CollectedDataNode partNode;

	public ChartComposite getFrame() {
		return frame;
	}

	public static Map<CollectedDataNode, Integer> getOpenedPlotMap() {
		return openedPlotMap;
	}

	public void setName(final String name) {
		setPartName(name);
	}

	public void setNode(final CollectedDataNode node) {
		partNode = node;
	}

	public static void addToOpenedPlotMap(final CollectedDataNode node,
			final int secondaryID) {
		openedPlotMap.put(node, secondaryID);
	}

	@Override
	public void createPartControl(Composite parent) {
		frame = new RDOChartComposite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);

		Slider verticalSlider = new Slider(parent, SWT.VERTICAL);
		FormData verticalSliderLayoutData = new FormData();

		Slider horizontalSlider = new Slider(parent, SWT.HORIZONTAL);
		FormData horizontalSliderLayoutData = new FormData();

		horizontalSliderLayoutData.left = new FormAttachment(0, 0);
		horizontalSliderLayoutData.bottom = new FormAttachment(100, 0);
		horizontalSliderLayoutData.right = new FormAttachment(verticalSlider);
		horizontalSlider.setLayoutData(horizontalSliderLayoutData);

		verticalSliderLayoutData.right = new FormAttachment(100, 0);
		verticalSliderLayoutData.bottom = new FormAttachment(horizontalSlider);
		verticalSliderLayoutData.top = new FormAttachment(0, 0);
		verticalSlider.setLayoutData(verticalSliderLayoutData);

		FormData graphLayoutData = new FormData();
		graphLayoutData.left = new FormAttachment(0, 0);
		graphLayoutData.right = new FormAttachment(verticalSlider);
		graphLayoutData.top = new FormAttachment(0, 0);
		graphLayoutData.bottom = new FormAttachment(horizontalSlider);
		frame.setLayoutData(graphLayoutData);

		parent.layout(true);
		horizontalSlider.setEnabled(false);
		horizontalSlider.setVisible(false);
		verticalSlider.setEnabled(false);
		verticalSlider.setVisible(false);
		frame.setSliders(horizontalSlider, verticalSlider);

		frame.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {
				if (!openedPlotMap.isEmpty()
						&& openedPlotMap.containsKey(partNode)) {
					openedPlotMap.remove(partNode);
				}
				if (partNode != null) {
					PlotDataParser.removeIndexFromMaps(partNode.getIndex());
				}
			}
		});
	}

	@Override
	public void setFocus() {
	}

	public void plotXY(final XYSeriesCollection dataset, List<String> enumNames) {
		final JFreeChart chart = createChart(dataset, enumNames);
		frame.setChart(chart);
		frame.setRangeZoomable(false);
	}

	private JFreeChart createChart(final XYDataset dataset,
			final List<String> enumNames) {

		final JFreeChart chart = ChartFactory.createXYStepChart("", "Time",
				"Value", dataset, PlotOrientation.VERTICAL, true, true, false);

		final XYPlot plot = (XYPlot) chart.getPlot();
		Color white = new Color(0xFF, 0XFF, 0xFF);
		plot.setBackgroundPaint(white);
		Color grey = new Color(0x99, 0x99, 0x99);
		plot.setDomainGridlinePaint(grey);
		plot.setRangeGridlinePaint(grey);
		plot.getRenderer().setSeriesStroke(0, new BasicStroke((float) 2.5));

		NumberAxis rangeAxis;
		if (enumNames != null) {
			String[] enumLabels = new String[enumNames.size()];
			enumLabels = enumNames.toArray(enumLabels);

			final FontRegistry fontRegistry = PlatformUI.getWorkbench()
					.getThemeManager().getCurrentTheme().getFontRegistry();
			final String fontName = fontRegistry.get(
					PreferenceConstants.EDITOR_TEXT_FONT).getFontData()[0]
					.getName();

			final Font font = new Font(fontName, Font.PLAIN, 10);
			rangeAxis = new SymbolAxis("", enumLabels);
			rangeAxis.setAutoRangeIncludesZero(true);
			plot.setRangeAxis(rangeAxis);
			rangeAxis.setTickLabelFont(font);

		} else {
			rangeAxis = new NumberAxis();
			rangeAxis.setAutoRangeIncludesZero(true);
			plot.setRangeAxis(rangeAxis);
		}

		final NumberAxis domainAxis = new NumberAxis();
		domainAxis.setAutoRangeIncludesZero(true);
		plot.setDomainAxis(domainAxis);

		double horizontalSliderMaximum = domainAxis.getRange().getLength();
		double verticalSliderMaximum = rangeAxis.getRange().getLength();
		frame.setSlidersMaximum(horizontalSliderMaximum, verticalSliderMaximum);

		return chart;
	}

}

class RDOChartComposite extends ChartComposite implements KeyListener {

	private Slider horizontalSlider;
	private Slider verticalSlider;

	public RDOChartComposite(Composite comp, int style) {
		super(comp, style, null, ChartComposite.DEFAULT_WIDTH,
				ChartComposite.DEFAULT_HEIGHT, 0, 0, Integer.MAX_VALUE,
				Integer.MAX_VALUE, ChartComposite.DEFAULT_BUFFER_USED, true,
				true, true, true, true);
		addSWTListener(this);
	}

	public void setSliders(Slider horizontalSlider, Slider verticalSlider) {
		this.horizontalSlider = horizontalSlider;
		this.verticalSlider = verticalSlider;
		horizontalSlider.setMinimum(0);
		verticalSlider.setMinimum(0);

		this.horizontalSlider.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getChart().getXYPlot().getDomainAxis()
						.setLowerBound(horizontalSlider.getSelection());
				getChart()
						.getXYPlot()
						.getDomainAxis()
						.setUpperBound(
								horizontalSlider.getThumb()
										+ horizontalSlider.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		this.verticalSlider.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getChart()
						.getXYPlot()
						.getRangeAxis()
						.setLowerBound(
								(verticalSlider.getMaximum()
										- verticalSlider.getSelection() - verticalSlider
										.getThumb()) / 100.0);
				getChart()
						.getXYPlot()
						.getRangeAxis()
						.setUpperBound(
								(verticalSlider.getMaximum() - verticalSlider
										.getSelection()) / 100.0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}

	public void setSlidersMaximum(double horizontalSliderMaximum,
			double verticalSliderMaximum) {
		horizontalSlider.setMaximum((int) horizontalSliderMaximum);
		verticalSlider.setMaximum((int) (verticalSliderMaximum * 100));

		if (getChart() != null) {
			XYPlot plot = getChart().getXYPlot();

			verticalSlider.setVisible(true);
			verticalSlider.setEnabled(true);
			verticalSlider.setThumb((int) Math
					.round((plot.getRangeAxis().getUpperBound() - plot
							.getRangeAxis().getLowerBound()) * 100));
			verticalSlider.setSelection((int) Math.round(verticalSlider
					.getMaximum() - plot.getRangeAxis().getUpperBound() * 100));
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.SHIFT) {
			this.setRangeZoomable(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.SHIFT) {
			this.setRangeZoomable(false);
		}
	}

	@Override
	public void zoom(Rectangle selection) {
		super.zoom(selection);

		XYPlot plot = getChart().getXYPlot();

		horizontalSlider.setVisible(true);
		horizontalSlider.setEnabled(true);
		horizontalSlider
				.setThumb((int) (plot.getDomainAxis().getUpperBound() - plot
						.getDomainAxis().getLowerBound()));
		horizontalSlider.setSelection((int) (plot.getDomainAxis()
				.getLowerBound()));

		if (this.isRangeZoomable()) {
			verticalSlider.setVisible(true);
			verticalSlider.setEnabled(true);
			verticalSlider.setThumb((int) Math
					.round((plot.getRangeAxis().getUpperBound() - plot
							.getRangeAxis().getLowerBound()) * 100));
			verticalSlider.setSelection((int) Math.round(verticalSlider
					.getMaximum() - plot.getRangeAxis().getUpperBound() * 100));
		}

	}

	@Override
	public void restoreAutoBounds() {
		super.restoreAutoBounds();
		horizontalSlider.setEnabled(false);
		horizontalSlider.setVisible(false);
		horizontalSlider.setMaximum((int) getChart().getXYPlot()
				.getDomainAxis().getRange().getLength());
		verticalSlider.setEnabled(false);
		verticalSlider.setVisible(false);
		verticalSlider.setMaximum((int) (getChart().getXYPlot().getRangeAxis()
				.getRange().getLength() * 100));
	}
}