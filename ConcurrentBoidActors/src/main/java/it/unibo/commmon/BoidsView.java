package it.unibo.commmon;

import akka.actor.typed.ActorRef;
import it.unibo.message.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import it.unibo.message.BoidMessage.*;

public class BoidsView implements ChangeListener {

	private JFrame frame;
	private BoidsPanel boidsPanel;
	private JSlider cohesionSlider, separationSlider, alignmentSlider;
	private JButton pauseResumeButton;
	private JButton startResetButton;
	private JTextField boidsNumberInput;
	private BoidsModel model;
	private int width, height;

	private ActorRef<BoidMessage> coordinator;
	
	public BoidsView(BoidsModel model, ActorRef<BoidMessage> coordinator, int width, int height) {
		this.model = model;
		this.coordinator = coordinator;
		this.width = width;
		this.height = height;

		frame = new JFrame("Boids Simulation");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);

        boidsPanel = new BoidsPanel(this, model);
		cp.add(BorderLayout.CENTER, boidsPanel);

		JPanel controlPanel = new JPanel();
        JPanel slidersPanel = new JPanel();


        cohesionSlider = makeSlider();
        separationSlider = makeSlider();
        alignmentSlider = makeSlider();
		pauseResumeButton = new JButton();
        pauseResumeButton.setText("Pause");
		pauseResumeButton.setEnabled(false);
		startResetButton = new JButton();
		startResetButton.setText("Start");

		boidsNumberInput = new JTextField(10);
		boidsNumberInput.setEnabled(true);
		boidsNumberInput.addActionListener(e -> {
            String input = boidsNumberInput.getText();
            int nBoids = Integer.parseInt(input);
			model.setNboids(nBoids);
			coordinator.tell(new BoidsChanged(nBoids));
			// simulator.notifyBoidsChanged();
        });

		pauseResumeButton.addActionListener(e -> {
            if (pauseResumeButton.getText().equals("Resume")){
				//simulator.notifyResumed();
				coordinator.tell(new Resume());
				boidsNumberInput.setEnabled(false);
			} else if (pauseResumeButton.getText().equals("Pause")) {
				// simulator.notifyStopped();
				coordinator.tell(new Stop());
				boidsNumberInput.setEnabled(true);
			}
			pauseResumeButton.setText(pauseResumeButton.getText().equals("Resume") ? "Pause" : "Resume");
        });

		startResetButton.addActionListener(e -> {
			if(startResetButton.getText().equals("Start")) {
				// simulator.notifyStarted();
				coordinator.tell(new BoidMessage.Start());
				startResetButton.setText("Reset");
				pauseResumeButton.setText("Pause");
				pauseResumeButton.setEnabled(true);
				boidsNumberInput.setEnabled(false);
			} else if (startResetButton.getText().equals("Reset")){
				// simulator.notifyResetted();
				coordinator.tell(new Reset());
				startResetButton.setText("Start");
				boidsNumberInput.setEnabled(true);
				pauseResumeButton.setEnabled(false);
			}

		});

		controlPanel.add(startResetButton);
		controlPanel.add(pauseResumeButton);
		controlPanel.add(new JLabel("Boids number"));
		controlPanel.add(boidsNumberInput);
        slidersPanel.add(new JLabel("Separation"));
        slidersPanel.add(separationSlider);
        slidersPanel.add(new JLabel("Alignment"));
        slidersPanel.add(alignmentSlider);
        slidersPanel.add(new JLabel("Cohesion"));
        slidersPanel.add(cohesionSlider);

		cp.add(BorderLayout.SOUTH, slidersPanel);
		cp.add(BorderLayout.NORTH, controlPanel);
		frame.setContentPane(cp);
        frame.setVisible(true);
	}

	private JSlider makeSlider() {
		var slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);        
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		Hashtable labelTable = new Hashtable<>();
		labelTable.put( 0, new JLabel("0") );
		labelTable.put( 10, new JLabel("1") );
		labelTable.put( 20, new JLabel("2") );
		slider.setLabelTable( labelTable );
		slider.setPaintLabels(true);
        slider.addChangeListener(this);
		return slider;
	}
	
	public void update(int frameRate) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				boidsPanel.setFrameRate(frameRate);
				boidsPanel.repaint();
			});
		} catch (InterruptedException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		coordinator.tell(new Resume());
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == separationSlider) {
			var val = separationSlider.getValue();
			coordinator.tell(new BoidMessage.SetSeparationWeight(0.1*val));

		} else if (e.getSource() == cohesionSlider) {
			var val = cohesionSlider.getValue();
			coordinator.tell(new BoidMessage.SetCohesionWeight(0.1*val));
		} else if (e.getSource() == alignmentSlider){
			var val = alignmentSlider.getValue();
			coordinator.tell(new BoidMessage.SetAlignmentWeight(0.1*val));
		}
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
